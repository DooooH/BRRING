import requests
from urllib.parse import urlparse, parse_qsl
from bs4 import BeautifulSoup
import firebase_admin
from firebase_admin import credentials
from firebase_admin import db
from datetime import datetime
import pytz

tz = pytz.timezone('Asia/Seoul')
cur_time = datetime.now(tz).strftime('%Y-%m-%d') # 년도-월-일 별로 가격
print(f'Seoul time: {cur_time}')

# Firebase database 인증 및 앱 초기화
cred = credentials.Certificate('key.json')
firebase_admin.initialize_app(cred, {
    'databaseURL': 'https://mobileappprogramming-3c71d-default-rtdb.firebaseio.com/'
})

headers = {'User-Agent' : 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.69 Safari/537.36'}
url = 'http://prod.danawa.com/info/?pcode=15253217&cate=12210596'
# pcode : 상품코드, cate : category

queries = dict(parse_qsl(urlparse(url).query))
prod = db.reference('product_list/' + queries['pcode'] + "/" + cur_time) # 상품코드별, 날짜별 위치 지정

response = requests.get(url, headers=headers)

if response.status_code == 200:
    html = response.text
    soup = BeautifulSoup(html, 'html.parser')

    # 최저가
    result = soup.find('em', class_='prc_c')
    prod.update({'min_price': result.get_text()})
    print("최저가:" + result.get_text())

    # 이미지
    img = soup.find('div', class_='photo_w')
    img_src = img.find('img')
    prod.update({'img': img_src['src']})
    print(img_src['src'])

    # 스펙
    spec = soup.find('div', class_='spec_list')
    # print("spec list")
    prod.update({'spec': spec.get_text().strip()})
    print(spec.get_text().strip())
    # spec_list = spec.find_all('a')
    # for item in spec_list:
    #     print(item.get_text())
