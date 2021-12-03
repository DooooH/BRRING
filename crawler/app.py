from flask import Flask, jsonify, request
import requests
from bs4 import BeautifulSoup
from urllib.parse import urlparse, parse_qsl

headers = {
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.69 Safari/537.36'}
# url = 'http://prod.danawa.com/list/?cate=12210596&logger_kw=ca_main_more'

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

            # 상품 코드
            prod_link = item.find('div', class_='thumb_image').find('a')
            url = prod_link.attrs['href']
            queries = dict(parse_qsl(urlparse(url).query))
            temp['no'] = queries['pcode']
            print(queries['pcode'])

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

@app.route('/search') #get search result
def get_search():
    param_dict = request.args.to_dict()
    if len(param_dict) == 0:
        return 'No parameter'

    keyword = request.args['item']
    url = 'http://search.danawa.com/dsearch.php?k1=' + keyword
    return jsonify(parse_search(url))

@app.route('/')
def hello():
    return 'Hello, World!'

app.run(host='0.0.0.0', port='5000')