import os
import requests
import pytz
import re
import time
from datetime import datetime
from urllib.parse import urlparse, parse_qsl
from bs4 import BeautifulSoup
import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore
from firebase_admin import db


def crawl_update(url_list, headers, cur_time):
    for url in url_list:
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
            # prod_ref.update({'img': img_src['src']})
            print(img_src['src'])

            # 이름
            name_tag = soup.find('h3', class_='prod_tit')
            name = name_tag.get_text()
            print(name)

            # 스펙
            spec = soup.find('div', class_='spec_list')
            # prod_ref.update({'spec': spec.get_text().strip()})
            print(spec.get_text().strip())

            docs = fs.collection(u'product_list').where(u'no', u'==', queries['pcode'])
            if len(docs.get()) > 0:
                doc = fs.collection(u'product_list').document(next(docs.stream()).id)
                doc.update({u'image_url': img_src['src'],
                                u'name': name,})
                print("exists")
            else:
                data = {
                    u'image_url': img_src['src'],
                    u'name': name,
                    u'no': queries['pcode'],
                    u'start_date': cur_time
                }
                prod_ref.add(data)
                print("Not found")



current_dir = os.path.dirname(os.path.realpath(__file__))

# firestore 인스턴스 초기화
cred = credentials.Certificate(current_dir+ '/key.json')
firebase_admin.initialize_app(cred, {
    'databaseURL': 'https://mobileappprogramming-3c71d-default-rtdb.firebaseio.com/'
}) # firestore & realtime db
fs = firestore.client()

headers = {'User-Agent' : 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.69 Safari/537.36'}
url_list = ['http://prod.danawa.com/info/?pcode=15253217',
            'http://prod.danawa.com/info/?pcode=7660909',
            'http://prod.danawa.com/info/?pcode=1117155',
            'http://prod.danawa.com/info/?pcode=1152054',
            'http://prod.danawa.com/info/?pcode=1151075']
# pcode : 상품코드, cate : category

prod_ref = fs.collection(u'product_list') # firestore

while True:
    tz = pytz.timezone('Asia/Seoul')
    cur_time = datetime.now(tz).strftime('%Y-%m-%d')  # 년도-월-일 별로 가격
    print(f'Seoul time: {cur_time}')
    crawl_update(url_list, headers, cur_time)
    time.sleep(600) # 10분
