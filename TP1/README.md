# INF8480
Distributed systems - Cloud

# TP1 -
## Partie 1 -

Étapes:

0. Aller dans dossier TP1\Partie_1\ResponseTime_Analyzer
1. Compiler le projet avec la commande : ant
2. cd dans le dossier : bin
3. Lancer la commande : rmiregistry &
4. Excuter le serveur : cd.. puis ./server.sh
	IL faut exécuter les étapes 0 à 4 pour le serveur distant et le serveur local.
5. Lancer le client sur la machine locale : ./client ipDistantServer x	
	x est la taille des arguments passés en paramètre lors de l'appel de fonction emptyFunc(). La taille est de l'ordre de 10^x.
    le client saura se connecté au serveur local (127.0.0.1), il ne requiert donc que l'adresse ip du serveur distant.
	

## Partie 2 - 

Étapes:

### Lancer le server d'authentification
0. Aller dans dossier TP1\Partie_2
1. Compiler le projet avec la commande : ant
2. cd dans le dossier : bin
3. Lancer la commande : rmiregistry &
4. Sortir du bin : cd ..
5. Excuter le serveur : ./serverAuth.sh 

### Lancer le server de gestion de fichiers
0. Aller dans dossier TP1/Partie_2
1. Executer le server : ./serverFileSystem.sh

### Partie Client
0. Lancer un client avec un authentifiant :
	-> ./client.sh new username password
	-> ./client.sh verify username password
1. Si l'étape 5 réussit, alors vous serez authentifier en temps que username (celui que vous avez donné) pour toutes les opérations subséquentes. Si une nouvelle authentification (new ou verify) a lieu, ce username sera maintenant l'utilisateur courant (current user). Ce faisant, pour tester deux clients, vous devrez suivre la démarche présentée en #2. 
2. Pour tester deux clients en même temps :
        -> Ouvrir un terminal à partir de TP1/Partie_2 et lancer client_1 avec un authentifiant (voir #0)
        -> Dans un autre terminal, aller dans TP1/Partie_2/Client_2
        -> Compiler le client_2 avec la commande : ant
        -> Lancer client_2 avec un authentifiant (voir #0)
        -> Utiliser les commandes présentes en #3

3. Voici les autres appels de fonction possible:
	-> ./client.sh create fileName
	-> ./client.sh list
	-> ./client.sh syncLocalDirectory
       -> ./client.sh syncLocalFiles
	-> ./client.sh get
	-> ./client.sh lock fileName
	-> ./client.sh push fileName
        
        * note : la commande syncLocalDirectory synchronise l'ensemble des fichiers présents sur le server avec le répertoire local du client, donc tous les fichiers du server sont rapatriés et se retrouvent dans le répertoire du client. La commande syncLocalFiles met à jour tous les fichiers du client avec leur version trouvée sur le server. On met donc à jour les fichiers du client sans rapatrier les fichiers qu'il ne possède pas déjà.


Les fichiers existants côté client sont dans le répertoire ./ClientSide/Files/. La liste des utilisateurs ayant réussi à se connecter au serveur exite aussi dans le répertoire ./ClientSide/ClientList.txt. Le fichier CurrentUser.txt contient le nom de l'utilisateur courant.
Les fichiers existants côté serveur sont dans le répertoire ./ServerSide/Files/. La liste des utilisateurs ayant réussi à se connecter au server se trouve dans le fichier ./ServerSide/ClientList.txt.  

### Troubleshoot
Si le terminal indique que vous n'avez pas les persmissions pour ce projet, exécuter la commande : chmod 777 ./myProject.sh
