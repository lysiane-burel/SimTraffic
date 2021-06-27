import xml.etree.ElementTree as ET
from xml.dom.minidom import parseString
import requests
import json
import csv
import sys
import os
import numpy as np
import pyproj




#Début du programme_______________________________________________________________________________________

#Récupération des coordonnées du cercle________________________________________
#parameters = sys.argv
#Param1 : coord1 48.8528904196
#Param2 : coord2 2.37074270681
#Param3 : rayon 200
#______________________________________________________________________________


class donneesjson():
	def __init__(self, data):
		self.__dict__ = json.loads(data)

def coordSumo(myroot, lat, lon):#TranslationOsmSumo

    for location in myroot.findall('location'):   
        decalageStr, param_proj = location.attrib['netOffset'], location.attrib['projParameter']
        decalage = decalageStr.split(',')
        
    p = pyproj.Proj(param_proj)
    point = p(lon, lat)
    decal_x, decal_y = float(decalage[0]), float(decalage[1])
    point = (round(point[0]+decal_x,2), round(point[1]+decal_y,2))
    return point



def clearAllRectangles(allRectangles, id_lane):
    #global allRectangles
    return [rectangle for rectangle in allRectangles if rectangle[0] != id_lane]

def idLane(allRectangles, detectorCoordinates): #CoordSumoToidLane

    for rectangle in allRectangles:

        lane_id, length, x1, y1, line, orth = rectangle

        detectorVector = np.array([detectorCoordinates[0] - x1, detectorCoordinates[1] - y1])

        if 0 <= np.vdot(line,detectorVector) <= length:# and abs(np.vdot(orth,detectorVector)) <= 3:

            clearAllRectangles(allRectangles, lane_id)
            return lane_id

    return '#'

def detectorsEdgeData(parameters1, parameters2, parameters3):
    #Recuperation du json contenant les détecteurs et leurs données________________
    url = "https://opendata.paris.fr/api/records/1.0/search/?dataset=comptages-routiers-permanents&q=&sort=t_1h&facet=q&geofilter.distance=++++{}%2C+++++{}%2C+{}".format(parameters1, parameters2, parameters3)
    donneestexte = requests.get(url)
    
    #Formatage en json_____________________________________________________________
    donnees = donneesjson(donneestexte.content)
    
    #Récupération des coordonnées de Carrefour.net.xml_____________________________
    filePath = "\Carrefour.net.xml"
    filePath = os.getcwd() + filePath
    carrefour = ET.parse(filePath)
    
    
    myroot = carrefour.getroot()
    
    ## creation liste :
    
    allRectangles = []
    
    #liste sous la forme : [ [id_lane, x1, y1, line, orth], [id_lane, x1, y1, line, orth] ]
    #id_lane en String, line et orth en np.array
    
    
    ## Récupération des coords de chaque lane et generation de allRectangles_______
    
    allRectangles = []
    
    for edge in myroot.findall('edge'):
    
        for lane in edge.findall('lane'):
            
            coordsPhrase = lane.attrib['shape']
    
            coords = coordsPhrase.split(" ")
            numberOfCoords = len(coords)
    
            for i in range(numberOfCoords - 1):
                coords1 = coords[i]
                coords2 = coords[i + 1]
    
    
                x1,y1 = coords1.split(",")
                x2,y2 = coords2.split(",")
    
                x1,y1,x2,y2 = float(x1), float(y1), float(x2), float(y2)
    
                line = np.array([x2 - x1, y2 - y1])
                length = np.linalg.norm(line)
    
                line = line / length
                cos, sin = np.vdot(line, np.array([1,0])), np.vdot(line, np.array([0,1]))
                orth = np.array([-sin, cos])
    
                allRectangles.append([lane.attrib['id'], length, x1, y1, line, orth])
    #______________________________________________________________________________
    
    detectors = ET.Element('detectors') #Pour le fichier Detectors.det.xml
    i=1
    moy=0
    with open('edgedata.csv', 'w', newline='') as csvfile: #Pour le fichier edgedata.csv
        data = csv.writer(csvfile, delimiter=';', quoting=csv.QUOTE_MINIMAL)
        data.writerow(['Detector', 'Time', 'qPKW']) #Premiere ligne de edgedata.csv
        
        for key in donnees.records:    #On regarde tous les détecteurs
            #Recherche de la route où se trouve le détecteur
            coordS = coordSumo(myroot, key['fields']['geo_point_2d'][0], key['fields']['geo_point_2d'][1])
            idL = idLane(allRectangles, coordS)
            
            #______________________________________________________________________
            if idL!='#': #Le '#' correspond à aucune lane trouvée
                try: #Parfois un détecteur ne possède pas de champ 'q'
                    q = str(int(key['fields']['q']))
                    moy=moy+int(q)
                    #Ligne dans Detectors.det.xml__________________________________
                    detectorsDefinition = ET.SubElement(detectors, 'detectorDefinition')
                    detectorsDefinition.set('id', str(i))
                    detectorsDefinition.set('lane', idL)
                    detectorsDefinition.set('pos', '0')
                    #Ligne dans edgedata.csv_______________________________________
                    data.writerow([str(i), '0', q])
                    i+=1
                except:
                    pass
                
        moy=moy/(i-1)     
        
        #Interpolation des données des routes ne comportant pas de détecteur (moyenne des valeurs des autres détecteurs)
        while len(allRectangles)>0:
                rectangle = allRectangles[0]
                detectorsDefinition = ET.SubElement(detectors, 'detectorDefinition')
                detectorsDefinition.set('id', str(i))
                detectorsDefinition.set('lane', rectangle[0])
                detectorsDefinition.set('pos', '0')
                #Ligne dans edgedata.csv_______________________________________
                data.writerow([str(i), '0', int(round(moy))])
                i+=1
                allRectangles = clearAllRectangles(allRectangles, rectangle[0]) #Il y avait plusieurs fois la même lane dans allRectangles
                
    
    #Ecriture du fichier Detectors.det.xml_________________________________________
    mydata = ET.tostring(detectors, encoding="unicode")
    parser = parseString(mydata)
    
    with open("Detectors.det.xml", "w") as myfile:
        myfile.write(parser.toprettyxml())
    #______________________________________________________________________________
"""
useless, parameters1, parameters2, parameters3 = sys.argv

detectorsEdgeData(parameters1, parameters2, parameters3)"""
