from google_images_search import GoogleImagesSearch

gis = GoogleImagesSearch()

queryString = 'pdf viewer icon'

queryStrings = ['pdf viewer icon', 'android icon', 'video player icon', 'google icon', 'google suite icon', 'chat app icon', 'social media icon', 'utility icon', 'office 365 icon', 'flash player icon']

for queryString in queryStrings:
    _search_params = {
        'q': queryString,
        'num': 5,
        'fileType': 'png|jpg',
    }

    gis.search(search_params=_search_params)

    for image in gis.results():
        # image.url  # image direct url
        # image.referrer_url  # image referrer url (source) 
        
        image.download('./iconImage/' + queryString)  # download image
        # image.resize(500, 500)  # resize downloaded image
        # image.path  # downloaded local file path

