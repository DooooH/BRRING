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
        url_docs = fs.collection(u'url_list').where(u'pcode', u'==', doc.to_dict()['no'])
        if len(url_docs.get()) > 0:
            url_doc =fs.collection(u'url_list').document(next(docs.stream()).id)
            print(f'Deleting doc {url_doc.id} => {url_doc.to_dict()}')
            url_doc.reference.delete()
        doc.reference.delete()

def crawl_update(url_list, headers, cur_time):
    delete_collection(recommendation_ref)

    for i in range(0, len(url_list)):
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

            # docs = fs.collection(u'recommendation_list').where(u'no', u'==', queries['pcode']) # recommendation 매번 삭제할 때는 검사안해도 됨
            docs = fs.collection(u'product_list').where(u'no', u'==', queries['pcode'])
            if len(docs.get()) > 0:
                # doc = fs.collection(u'recommendation_list').document(next(docs.stream()).id)
                doc = fs.collection(u'product_list').document(next(docs.stream()).id)
                doc.update({u'image_url': img_src['src'],
                                u'name': name,})
                print("exists")
            else:
                # prod_ref 용 data
                data = {
                    u'image_url': img_src['src'],
                    u'name': name,
                    u'no': queries['pcode'],
                    u'price': new_price,
                    u'start_date': cur_time
                }
                prod_ref.add(data)
                data[u'price'] = new_price # recommendation 용 data
                recommendation_ref.add(data)
                url_data = {
                    u'pcode': queries['pcode'],
                    u'url': url
                }
                url_ref.add(url_data)
                print("Not found")

def recommend_crawl_update(headers, cur_time):
    url = 'http://www.danawa.com/'

    driver = webdriver.Chrome(executable_path= r'./chromedriver') # mac
    driver.implicitly_wait(100)
    driver.get(url)
    driver.implicitly_wait(1000)

    html = driver.page_source
    soup = BeautifulSoup(html, 'html.parser')
    driver.close()
    # print(soup.prettify())
    total = soup.find('div', class_='main-pick cmPick-swiper')

    a_list = soup.find_all('a', class_='prod-list__link')

    # print(total.prettify())

    url_list = []

    for a in a_list:
        href = a.attrs['href']
        queries = dict(parse_qsl(urlparse(href).query))
        # print(href)
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
prod_ref = fs.collection(u'product_list') # firestore
url_ref = fs.collection(u'url_list') # firestore

while True:
    tz = pytz.timezone('Asia/Seoul')
    cur_time = datetime.now(tz).strftime('%Y-%m-%d')  # 년도-월-일 별로 가격
    print(f'Seoul time: {cur_time}')
    recommend_crawl_update(headers, cur_time)
    time.sleep(3600 * 6) # 6시간
