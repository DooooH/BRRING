import requests
from bs4 import BeautifulSoup
# from selenium import webdriver

# driver = webdriver.Chrome('chromedriver.exe')
headers = {'User-Agent' : 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.69 Safari/537.36'}
url = 'http://prod.danawa.com/list/?cate=12210596&logger_kw=ca_main_more'

response = requests.get(url, headers=headers)

if response.status_code == 200:
    html = response.text
    soup = BeautifulSoup(html, 'html.parser')
    # list = driver.find_element_by_class_name('product_list').text

    # print(soup)
    result = soup.find('div', class_='main_prodlist main_prodlist_list')
    # print(result)

    product_list = result.find_all('ul', class_='product_list')

    for item in product_list:
        print(item.find('p', class_='prod_name').get_text())
        print("DIV__________________")

