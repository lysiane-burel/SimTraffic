import requests
import sys
import pyproj
import xml.etree.ElementTree as ET
import csv 
import matplotlib.pyplot as plt
import matplotlib.image as img

##

accessToken = "pk.eyJ1IjoiY2xlbXRjIiwiYSI6ImNrbmE0YnVrcDFlOTcycnA5azF0MWg5MXkifQ.fzcQrtnN3mdOvi7UKtW3og"
##

#Récupération des coordonnées de Carrefour.net.xml_____________________________
filePath = "Carrefour.net.xml"
carrefour = ET.parse(filePath)


myroot = carrefour.getroot()

## pour connaitre le nombre de pixel nécessaire dans l'image

def coordSumo(lat, lon):#TranslationOsmSumo

    for location in myroot.findall('location'):
        decalageStr, param_proj = location.attrib['netOffset'], location.attrib['projParameter']
        decalage = decalageStr.split(',')

    p = pyproj.Proj(param_proj)
    point = p(lon, lat)
    decal_x, decal_y = float(decalage[0]), float(decalage[1])
    point = (round(point[0]+decal_x,2), round(point[1]+decal_y,2))
    return point

##

def downloadMapImage(minlon, minlat, maxlon, maxlat):

    global pixelPerMeter, height, width
    # global minX,minY,maxX,maxY,height,width

    minX, minY = coordSumo(minlat, minlon)
    maxX, maxY = coordSumo(maxlat, maxlon)

    delta = [abs(maxX-minX), abs(maxY - minY)]

    #width, height = min(1280,2   * abs(maxX-minX)), min(1280,2 * abs(maxY - minY))

    #width, height = str(int(width)), str(int(height))

    
    pixelPerMeter = 1280.0 / max(delta)

    width = int(pixelPerMeter * delta[0])
    height = int(pixelPerMeter * delta[1])

    width, height = str(int(width)), str(int(height))
    #print("w=", width, " h=", height)
    

    url = 'https://api.mapbox.com/styles/v1/mapbox/streets-v11/static/['
    url = url + str(minlon) + ',' + str(minlat) + ',' + str(maxlon) + ',' + str(maxlat)
    url = url + ']/'
    url = url + width + 'x' + height + '?access_token='
    url = url + accessToken

    #print(url)
    r = requests.get(url, allow_redirects=True)
    open('map.png', 'wb').write(r.content)

##
    
def pixelCoords(xSumo, ySumo):
    xOrgSumo, yOrgSumo = coordSumo(minlat,minlon)
    x = xSumo - xOrgSumo
    y = ySumo - yOrgSumo
    xImage = int(x * pixelPerMeter)
    yImage = int(int(height) - y * pixelPerMeter)
    return xImage, yImage

## recuperation des coordonnees passees en argument
#correct use : python downloadImage.py minlon minlat maxlon maxlat

if len(sys.argv) != 5:
    print("Error : bad number of arguments.")
    print("correct use : python affichageResultat.py minlon minlat maxlon maxlat")
    print("affichageResultat.py stopped")
    exit()



useless, minlon, minlat, maxlon, maxlat = sys.argv



downloadMapImage(minlon, minlat, maxlon, maxlat)


##
#test with 2.34992,48.83571,2.35592,48.83748

#downloadMapImage(2.34992,48.83571,2.35592,48.83748)



## tentative d'ajout d'un point sur une image

def ajoutPoint(x, y):
    xImage, yImage = pixelCoords(x,y)
    plt.scatter(xImage, yImage, color='r')

def ajoutTexte(x, y, text):
    xImage, yImage = pixelCoords(x,y)
    plt.text(xImage, yImage, text)


##Affichage de l'image
image = img.imread('map.png')
plt.imshow(image)


## lecture .csv

#filePath = os.getcwd() + '/output.csv'
filePath = 'output.csv'
meanSpeed = 0

with open(filePath, newline='') as csvfile:
    spamreader = csv.reader(csvfile, delimiter=',', quotechar='|')
    for row in spamreader:
        #print(row)
        if  len(row) > 1:
            frow2=0
            frow1=0
            try:
                frow2=float(row[-2])
                frow1=float(row[-1])
            except:
                pass
            #print(len(row))
            ajoutPoint(frow2,frow1)

            n = int(row[0])
            #print(n)

            text = ""

            for i in range(n):
                #print(row[n+i+1])
                text = text + row[n+i+1] + ", "

            ajoutTexte(frow2,frow1,text[:-2])
            #plt.text(frow2,frow1,text[:-2])

        else:
            meanSpeed = float(row[0])

## lecture deuxieme csv

#filePath = os.getcwd() + '/previousMeanSpeed.csv'
filePath = 'previousMeanSpeed.csv'
previousMeanSpeed = 0

with open(filePath, newline='') as csvfile:
    spamreader = csv.reader(csvfile, delimiter=',', quotechar='|')
    for row in spamreader:
        previousMeanSpeed = float(row[0])

print(previousMeanSpeed)



##

previousMeanSpeed = "Vitesse moyenne avant optimisation : " + str(round(previousMeanSpeed*3.6,1)) + " km/h"
meanSpeed = "Vitesse moyenne après optimisation : " + str(round(meanSpeed*3.6,1)) + " km/h"

plt.text(0,-30,str(previousMeanSpeed))
plt.text(0, int(height) + 60,str(meanSpeed))
plt.axis('off')
plt.savefig("Result.png")
plt.show()
