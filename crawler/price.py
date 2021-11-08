import requests
from bs4 import BeautifulSoup

headers = {'User-Agent' : 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.69 Safari/537.36'}
url = 'http://prod.danawa.com/info/?pcode=15253217&cate=12210596'

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
