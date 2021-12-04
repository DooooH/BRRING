import os
import re
import pytz
from datetime import datetime
from flask import Flask, jsonify, request
import requests
from bs4 import BeautifulSoup
from urllib.parse import urlparse, parse_qsl
import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore
from firebase_admin import db

headers = {
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.69 Safari/537.36'}
# url = 'http://prod.danawa.com/list/?cate=12210596&logger_kw=ca_main_more'

def new_crawl(url_list, headers, cur_time, pcode):
    for url in url_list:
        price_ref = db.reference('product_list/' + pcode + '/price')  # realtime db

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

            data = {
                u'image_url': img_src['src'],
                u'name': name,
                u'no': pcode,
                u'price': new_price,
                u'start_date': cur_time
            }
            prod_ref.add(data)
            url_data = {
                u'pcode': pcode,
                u'url': url
            }
            url_ref.add(url_data)
            print("Not found")

def parse_search(url):
    response = requests.get(url, headers=headers)

    data = {'contents': []}

    if response.status_code == 200:
        html = response.text
        soup = BeautifulSoup(html, 'html.parser')

        product_list = soup.find('ul', class_='product_list')

        # 각 상품 정보
        prod_info_list = product_list.find_all('div', class_='prod_main_info')

        for i in range(0, len(prod_info_list)):
            item = prod_info_list[i]
            temp = {}

            # 상품 코드
            prod_link = item.find('div', class_='thumb_image').find('a')
            url = prod_link.attrs['href']
            print(url)
            queries = dict(parse_qsl(urlparse(url).query))
            if 'pcode' not in queries: # 외부 링크 제외
                continue
            temp['no'] = queries['pcode']
            print(queries['pcode'])

            # 각 상품 이미지
            prod_img = item.find('div', class_='thumb_image').find('img')
            img = ''
            if prod_img.has_attr('data-original'):
                img = prod_img['data-original']
                print(prod_img['data-original'])
            else:
                img = prod_img['src']
                print(prod_img['src'])
            if 'noImg' in img:
                continue
            temp["img"] = img

            # 인기순위, 상품이름 포함
            prod_name = item.find('p', class_='prod_name')
            # 상품명
            name = prod_name.find('a').get_text().strip()
            temp['name'] = name
            print(name)

            # 스펙
            prod_spec = item.find('div', class_='spec_list')
            spec = prod_spec.get_text().strip()
            temp['spec'] = spec
            print(spec)
            
            # 가격
            prod_price = item.find('li', class_='rank_one')
            if prod_price is not None:
                prod_price = prod_price.find('p', class_='price_sect').find('a')
            else:
                prod_price = item.find('li', class_='top5_item').find('div', class_='top5_price')
            price = prod_price.get_text().strip()
            temp['price'] = price
            print(price)

            # list append
            data['contents'].append(temp)

    print(data)
    return data

app = Flask(__name__)

current_dir = os.path.dirname(os.path.realpath(__file__))
headers = {'User-Agent' : 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.69 Safari/537.36'}

# firestore 인스턴스 초기화
cred = credentials.Certificate(current_dir+ '/key.json')
firebase_admin.initialize_app(cred, {
    'databaseURL': 'https://mobileappprogramming-3c71d-default-rtdb.firebaseio.com/'
}) # firestore & realtime db
fs = firestore.client()
prod_ref = fs.collection(u'product_list') # firestore
url_ref = fs.collection(u'url_list') # firestore

@app.route('/search') #get search result
def get_search():
    param_dict = request.args.to_dict()
    if len(param_dict) == 0:
        return 'No parameter'

    keyword = request.args['item']
    url = 'http://search.danawa.com/dsearch.php?k1=' + keyword
    return jsonify(parse_search(url))

@app.route('/product') #add new product
def get_product():
    param_dict = request.args.to_dict()
    if len(param_dict) == 0:
        return 'No parameter'

    pcode = request.args['no']
    docs = fs.collection(u'product_list').where(u'no', u'==', pcode)
    if len(docs.get()) == 0:
        tz = pytz.timezone('Asia/Seoul')
        cur_time = datetime.now(tz).strftime('%Y-%m-%d')  # 년도-월-일 별로 가격
        print(f'Seoul time: {cur_time}')
        url_list = ['http://prod.danawa.com/info/?pcode=' + pcode]
        new_crawl(url_list, headers, cur_time, pcode)
    return 'Success'

@app.route('/')
def hello():
    return 'Hello, World!'

app.run(host='0.0.0.0', port='5000')