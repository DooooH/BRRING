import requests
import pytz
import re
import time
from datetime import datetime
from urllib.parse import urlparse, parse_qsl
from bs4 import BeautifulSoup
import firebase_admin
from firebase_admin import credentials
from firebase_admin import db

def crawl_update(url, headers, cur_time):
    response = requests.get(url, headers=headers)

    if response.status_code == 200:
        html = response.text
        soup = BeautifulSoup(html, 'html.parser')

        # 가격
        # 새로 가져온 가격이 더 낮을 때만 갱신
        result = soup.find('em', class_='prc_c')
        new_price = int(re.sub(r'[^0-9]', '', result.get_text()))
        try :
            prev = price_ref.get(cur_time)[0][cur_time]
            if new_price < prev:
                price_ref.update({cur_time: new_price})
        except:
            price_ref.update({cur_time: new_price})
        print("가격: ", new_price)

        # 최저가
        try:
            min_price = prod_ref.child('min_price').get()
            # print(min_price)
            if new_price < min_price:
                prod_ref.update({'min_price': new_price})
        except:
            prod_ref.update({'min_price': new_price}) # 이 전 가격이 없을 때
        print("최저가: ", prod_ref.child('min_price').get())

        # 이미지
        img = soup.find('div', class_='photo_w')
        img_src = img.find('img')
        prod_ref.update({'img': img_src['src']})
        print(img_src['src'])

        # 스펙
        spec = soup.find('div', class_='spec_list')
        prod_ref.update({'spec': spec.get_text().strip()})
        print(spec.get_text().strip())

# Firebase database 인증 및 앱 초기화
cred = credentials.Certificate('key.json')
firebase_admin.initialize_app(cred, {
    'databaseURL': 'https://mobileappprogramming-3c71d-default-rtdb.firebaseio.com/'
})

headers = {'User-Agent' : 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.69 Safari/537.36'}
url = 'http://prod.danawa.com/info/?pcode=15253217&cate=12210596'
# pcode : 상품코드, cate : category

queries = dict(parse_qsl(urlparse(url).query))
prod_ref = db.reference('product_list/' + queries['pcode']) # 상품코드별, 날짜별 위치 지정
price_ref = prod_ref.child('price')

while True:
    tz = pytz.timezone('Asia/Seoul')
    cur_time = datetime.now(tz).strftime('%Y-%m-%d')  # 년도-월-일 별로 가격
    print(f'Seoul time: {cur_time}')
    crawl_update(url, headers, cur_time)
    time.sleep(600) # 10분
