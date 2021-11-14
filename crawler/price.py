import requests
from bs4 import BeautifulSoup
import firebase_admin
from firebase_admin import credentials
from firebase_admin import db

# Firebase database 인증 및 앱 초기화
cred = credentials.Certificate('key.json')
firebase_admin.initialize_app(cred, {
    'databaseURL': 'https://mobileappprogramming-3c71d-default-rtdb.firebaseio.com/'
})

dir = db.reference() #기본 위치 지정
dir.update({'자동차':'기아'})

headers = {'User-Agent' : 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.69 Safari/537.36'}
url = 'http://prod.danawa.com/info/?pcode=15253217&cate=12210596'
# pcode : 상품코드, cate : category


# config = {
#     "apiKey": "AIzaSyD6fVZiXJbrh45bKVJDfHcDxMJU4LhVbp8",
#     "authDomain": "pythontest.firebaseapp.com",
#     "databaseURL": "https://mobileappprogramming-3c71d-default-rtdb.firebaseio.com",
#     "projectId": "mobileappprogramming-3c71d",
#     "storageBucket": "mobileappprogramming-3c71d.appspot.com",
#     "messagingSenderId": "6",
#     "appId": ""
# }


response = requests.get(url, headers=headers)

if response.status_code == 200:
    html = response.text
    soup = BeautifulSoup(html, 'html.parser')

    # 최저가
    result = soup.find('em', class_='prc_c')
    print("최저가:" + result.get_text())

    # 이미지
    img = soup.find('div', class_='photo_w')
    img_src = img.find('img')
    print(img_src['src'])

    # 스펙
    spec = soup.find('div', class_='spec_list')
    spec_list = spec.find_all('a')
    print("spec list")
    for item in spec_list:
        print(item.get_text())
