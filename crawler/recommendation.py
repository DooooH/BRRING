import os
import requests
import pytz
import re
import time
from datetime import datetime
from urllib.parse import urlparse, parse_qsl
from bs4 import BeautifulSoup
from selenium import webdriver
import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore
from firebase_admin import db

def delete_collection(coll_ref):
    docs = coll_ref.stream()

    for doc in docs:
        print(f'Deleting doc {doc.id} => {doc.to_dict()}')
        doc.reference.delete()

def crawl_update(url_list, headers, cur_time):
    delete_collection(recommendation_ref)

    for i in range(0, 10):
        url = url_list[i]
        queries = dict(parse_qsl(urlparse(url).query))
        price_ref = db.reference('product_list/' + queries['pcode'] + '/price')  # realtime db

        response = requests.get(url, headers=headers)

        if response.status_code == 200:
            html = response.text
            soup = BeautifulSoup(html, 'html.parser')

            # 가격
            # 새로 가져온 가격이 더 낮을 때만 갱신
            # realtime db 사용
            result = soup.find('em', class_='prc_c')
            new_price = int(re.sub(r'[^0-9]', '', result.get_text()))
            try :
                prev = price_ref.get(cur_time)[0][cur_time]
                if new_price < prev:
                    price_ref.update({cur_time: new_price})
            except:
                price_ref.update({cur_time: new_price})
            print("가격: ", new_price)

            # 이미지
            img = soup.find('div', class_='photo_w')
            img_src = img.find('img')
            print(img_src['src'])

            # 이름
            name_tag = soup.find('h3', class_='prod_tit')
            name = name_tag.get_text()
            print(name)

            # 스펙
            spec = soup.find('div', class_='spec_list')
            print(spec.get_text().strip())

            docs = fs.collection(u'recommendation_list').where(u'no', u'==', queries['pcode'])
            if len(docs.get()) > 0:
                doc = fs.collection(u'recommendation_list').document(next(docs.stream()).id)
                doc.update({u'image_url': img_src['src'],
                                u'name': name,})
                print("exists")
            else:
                data = {
                    u'image_url': img_src['src'],
                    u'name': name,
                    u'no': queries['pcode'],
                    u'price': new_price,
                    u'start_date': cur_time
                }
                recommendation_ref.add(data)
                print("Not found")

def recommend_crawl_update(headers, cur_time):
    url = 'http://www.danawa.com/'

    driver = webdriver.Chrome(executable_path= r'./chromedriver') # mac
    driver.get(url)

    html = driver.page_source
    driver.close()
    soup = BeautifulSoup(html, 'html.parser')

    total = soup.find('div', class_='main-pick cmPick-swiper')

    a_list = soup.find_all('a', class_='prod-list__link')

    url_list = []

    for a in a_list:
        href = a.attrs['href']
        queries = dict(parse_qsl(urlparse(href).query))
        if "pcode" in queries:
            # print(queries["pcode"])
            url_list.append(href)

    crawl_update(url_list, headers, cur_time)

current_dir = os.path.dirname(os.path.realpath(__file__))

# firestore 인스턴스 초기화
cred = credentials.Certificate(current_dir+ '/key.json')
firebase_admin.initialize_app(cred, {
    'databaseURL': 'https://mobileappprogramming-3c71d-default-rtdb.firebaseio.com/'
}) # firestore & realtime db
fs = firestore.client()

headers = {'User-Agent' : 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.69 Safari/537.36'}

# pcode : 상품코드, cate : category

recommendation_ref = fs.collection(u'recommendation_list') # firestore

while True:
    tz = pytz.timezone('Asia/Seoul')
    cur_time = datetime.now(tz).strftime('%Y-%m-%d')  # 년도-월-일 별로 가격
    print(f'Seoul time: {cur_time}')
    recommend_crawl_update(headers, cur_time)
    time.sleep(3600 * 6) # 6시간
