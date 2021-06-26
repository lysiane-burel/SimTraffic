# Programme global

import sys
import os
import time
from downloadOsmfromCoords import downloadOsm
from Ecriture_Detectors_EdgeData import detectorsEdgeData
from sumocfg_maker import sumocfg


t0 = time.perf_counter()

#Réccupération des coordonées
#Liste [longitude1, latitude1, longitude2, latitude2, latCercle, longCercle, rayon]
parametres = sys.argv

#Exécution de downloadOsmfromCoords pour créer le Carrefour.net.xml
#os.system('cmd /c "python downloadOsmfromCoords.py {} {} {} {}"'.format(parametres[1], parametres[2], parametres[3], parametres[4]))
downloadOsm(parametres[1], parametres[2], parametres[3], parametres[4])


tosm = time.perf_counter()
print("OSM : ", tosm-t0)

#Exécution de Ecriture_Detectors_EdgeData.py pour créer Detectors.det.xml et edgedata.csv
#os.system('cmd /c "python Ecriture_Detectors_EdgeData.py {} {} {}"'.format(parametres[5], parametres[6], parametres[7]))

detectorsEdgeData(parametres[5], parametres[6], parametres[7])

tdata = time.perf_counter()
print("Edge Data : ", tdata-tosm)

#Exécution de sumocfg_maker.py pour créer flows.rou.xml et Simulation.sumocfg
#os.system('cmd /c "python sumocfg_maker.py"')
sumocfg()

tsumo = time.perf_counter()
print("Sumocfg : ", tsumo - tdata)

#Exécute Main.java pour créer un fichier sumocfg, lancer la simulation
#Apporter les modifications et enfin communiquer les modifications à faire pour visualiser les données
#os.system('cmd /c "java -jar Optimisation.jar"')
#os.system('cmd /c "java Main"')
"""

#Visualisation des résultats
os.system('cmd /c "python affichageResultat.py {} {} {} {}"'.format(parametres[1], parametres[2], parametres[3], parametres[4]))

"""
tres = time.perf_counter()
print("Affichage : ", tres-tsumo)
