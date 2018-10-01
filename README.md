# INF8480
Distributed systems - Cloud

#TP1 -
##Partie 1 -

Étapes:

0. Aller dans dossier TP1\Partie_1\ResponseTime_Analyzer
1. Compiler le projet avec la commande : ant
2. cd dans le dossier : bin
3. Lancer la commande : rmiregistry &
4. Excuter le serveur : ./server.sh
	Si le serveur est exécuter sur une machine distante, garder en note son adresse ip
5. Lancer le client sur la machine locale : ./client ipaddress x
	ipaddress - 127.0.0.1 pour une machine locale, sinon adresse ip du serveur distante
	x est la taille des arguments passés en paramètre lors de l'appel de fonction emptyFunc(). La taille est de l'ordre de 10^x.
	

##Partie 2 - 

Étapes:

0. Aller dans dossier TP1\Partie_2
1. Compiler le projet avec la commande : ant
2. cd dans le dossier : bin
3. Lancer la commande : rmiregistry &
4. Excuter le serveur : ./server.sh
5. Lancer un client avec un authentifiant :
	-> ./client.sh new username password
	-> ./client.sh verify username password
6. Si l'étape 5 réussit, alors vous serez authentifier en temps que username (celui que vous avez donné) pour toutes les opérations subséquentes. Si une nouvelle authentification (new ou verify) a lieu, ce username sera maintenant l'utilisateur courant (current user).
7. Voici les autres appels de fonction possible:
	-> ./client.sh create fileName
	-> ./client.sh list
	-> ./client.sh syncLocalDirectory
	-> ./client.sh get
	-> ./client.sh lock fileName
	-> ./client.sh push fileName
	
Les fichiers existants côté client sont dans le répertoire Partie_2\ClientSide\Files. Il exite aussi dans le répertoire Partie_2\ClientSide, la liste des utilisaterus qui ont réussi à se connecter au serveur (ClientList.txt) et le fichier CurrentUser.txt qui contient le nom de l'utilisateur courant.
Les fichiers existants côté serveur sont dans le répertoire Partie_2\ServerSide\Files. Il existe aussi dans le répertoire Partie_2\ServerSide\Files, la liste des utilisateurs qui ont réussi à se connecter au server dans le fichier ClientList.txt.
