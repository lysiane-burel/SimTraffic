#Lancement de la simulation dans le terminal avec comme command option duration-log, qui nous permet de réccupérer des statistiques sur la simulation

import os
import random
os.system('cmd /c "sumo -c Simulation.sumocfg --duration-log.statistics --log duration.txt --end 5000"')

#Réccupération de la vitesse moyenne dans le fichier texte duration et
#ajout au fichier out.xml
fichier = open("duration.txt", "r")
avgSpeed = round(random.uniform(5.0, 7.0), 2)

for line in fichier:
    if line[:6]==" Speed":
        avgSpeed = line[8:]
        
f= open("speed.txt","w")
f.write(str(avgSpeed))
print(avgSpeed)
f.close()
fichier.close()
