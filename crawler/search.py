import requests
from bs4 import BeautifulSoup

# from selenium import webdriver

# driver = webdriver.Chrome('chromedriver.exe')
headers = {
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.69 Safari/537.36'}
url = 'http://prod.danawa.com/list/?cate=12210596&logger_kw=ca_main_more'

response = requests.get(url, headers=headers)

if response.status_code == 200:
    html = response.text
    soup = BeautifulSoup(html, 'html.parser')
    # list = driver.find_element_by_class_name('product_list').text

    product_list = soup.find('ul', class_='product_list')

    # 각 상품 정보
    prod_info_list = product_list.find_all('div', class_='prod_main_info')

    for i in range(0, len(prod_info_list) - 1):
        item = prod_info_list[i]
        # 인기순위, 상품이름 포함
        prod_name = item.find('p', class_='prod_name')
        # 상품명
        print(prod_name.find('a').get_text().strip())

        # 각 상품 이미지
        prod_img = item.find('div', class_='thumb_image').find('img')
        if prod_img.has_attr('data-original'):
            print(prod_img['data-original'])
        else:
            print(prod_img['src'])

        # 스펙
        prod_spec = item.find('div', class_='spec_list')
        print(prod_spec.get_text().strip())
