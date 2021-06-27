import requests
import sys
import os

##


def downloadOsm(minlon, minlat, maxlon, maxlat):
    url = 'https://overpass-api.de/api/map?bbox='
    url = url + str(minlon) + ',' + str(minlat) + ',' + str(maxlon) + ',' + str(maxlat)
    print(url)
    r = requests.get(url, allow_redirects=True)
    open('map.osm', 'wb').write(r.content)
    os.system("netconvert --osm-files map.osm -o Carrefour.net.xml --geometry.remove --ramps.guess --junctions.join --tls.guess-signals --tls.discard-simple --tls.join --tls.default-type static --keep-edges.by-vclass passenger --plain-output-prefix")

#test with 2.34992 48.83571 2.35592 48.83748


## recuperation des coordonnees passees en argument
#correct use : python downloadOsmfromCoords.py minlon minlat maxlon maxlat
"""
if len(sys.argv) != 5:
    print("Error : bad number of arguments.")
    print("correct use : python downloadOsmfromCoords.py minlon minlat maxlon maxlat")
    print("downloadOsmfromCoords.py stopped")
    exit()
"""

"""
useless, minlon, minlat, maxlon, maxlat = sys.argv   
downloadOsm(minlon, minlat, maxlon, maxlat)
"""    
    
