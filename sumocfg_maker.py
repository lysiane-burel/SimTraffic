#Entrée 1 : Carrefour.net.xml
#Entrée 2 : Detectors.det.xml
#Entrée 3 : edgedata.csv.csv

#Sortie 1 : routes.rou.xml
#Sortie 2 : vehicles.xml
#Sortie 3 : flows.rou.xml
#Sortie 4 : Simulation.sumocfg



from xml.dom import minidom
import xml.etree.ElementTree as ET
import os
from xml.dom.minidom import parseString
import csv


def sumocfg():
    os.system('cmd /c "dfrouter --net-file Carrefour.net.xml -d Detectors.det.xml -f edgedata.csv --routes-output routes.rou.xml --emitters-output vehicles.xml"')
    #__________________________________________________________________________________________
    
    #Recuperation des donnees des fichiers crees par les fonctions SUMO________________________
    routesData = minidom.parse("routes.rou.xml").getElementsByTagName('route')
    vehcData = minidom.parse("vehicles.xml").getElementsByTagName('route')
    
    roads = dict()
    edges = dict()
    probabilities = dict()
    
    #Récupération du nombre de voitures par heure____________________________________________
    with open('edgedata.csv', newline='') as csvfile :
        spamreader = csv.reader(csvfile, delimiter=';', quotechar='|')
        for row in spamreader:
            try :
                if not row[0]=='Detector':
                    edges[row[0]] = float(row[2])
            except:
                pass
    #___________________________________________________________________________________________
    
    for elem in routesData:
        roads[elem.attributes['id'].value] = elem.attributes['edges'].value
    
    for elem in vehcData:
        probabilities[elem.attributes['refId'].value] = float(elem.attributes['probability'].value)
    #___________________________________________________________________________________________
    
    #Creation du fichier flows.rou.xml__________________________________________________________
    routes = ET.Element('routes')
    
    vType = ET.SubElement(routes, 'vType')
    vType.set('accel', '3.0')
    vType.set('decel', '6.0')
    vType.set('id', 'CarA')
    vType.set('length', '5.0')
    vType.set('minGap', '2.5')
    vType.set('maxSpeed', '50.0')
    vType.set('sigma', '0.5')
    
    i=1
    for id in probabilities.keys():
    
        proba = probabilities[id]
        if proba != 0:
            flow = ET.SubElement(routes, 'flow')
            flow.set('id', 'flow'+id)
            flow.set('begin', '0')
            flow.set('end', '3600000')
            nbVehs = proba*edges[str(i)] #Calcul du nombre de vehicules par heure d'apres les probabilites
            if nbVehs - int(nbVehs) < 0.5:
                flow.set('vehsPerHour', str(int(nbVehs)))
            else:
                flow.set('vehsPerHour', str(int(nbVehs)+1))
            route = ET.SubElement(flow, 'route')
            route.set('id', 'route'+id)
            route.set('edges', roads[id])
        i+=1
    #__________________________________________________________________________________________
    
    
    # Ecriture du fichier flows.rou.xml________________________________________________________
    mydata = ET.tostring(routes, encoding="unicode")
    parser = parseString(mydata)
    
    with open("flows.rou.xml", "w") as myfile:
        myfile.write(parser.toprettyxml())
    #__________________________________________________________________________________________   
        
    #Creation du fichier Simulation.sumocfg____________________________________________________
    configuration = ET.Element('configuration')
    inputt = ET.SubElement(configuration, 'input')
    netfile = ET.SubElement(inputt, 'net-file') 
    netfile.set('value', 'Carrefour.net.xml')
    routefiles = ET.SubElement(inputt, 'route-files') 
    routefiles.set('value', 'flows.rou.xml')
    time = ET.SubElement(configuration, 'time')
    begin = ET.SubElement(time, 'begin') 
    begin.set('value', '000')
    end = ET.SubElement(time, 'end') 
    end.set('value', '3600000')
    #__________________________________________________________________________________________ 
    
    #Ecriture du fichier Simulation.sumocfg____________________________________________________
    mydata = ET.tostring(configuration, encoding="unicode")
    parser = parseString(mydata)
    
    with open("Simulation.sumocfg", "w") as myfile:
        myfile.write(parser.toprettyxml())
    #__________________________________________________________________________________________ 
