# INF8480
Distributed systems - Cloud

# TP2 -

Nous avons créé des fichiers JSON pour la configuration des différentes parties du système. Tous les JSON sont présents dans le dossier configs.

*Les Serveurs*
dans operationServer/Server_X.json :
Vous pouvez y mettre le C, le m, le port et le host de votre serveur

*Le dispatcher* 
dans dispatcher_configs.json
Vous devez spécifier les credentials de votre dispatcher, son moed de sécurité, le nom du fichier d'opération à effectuer (ce fichier doit être mis dans le dossier ./operationFiles), 
les serveurs que vous voulez et leur nombre. Il faut s'assurer que les caractéristiques de chacun corresponde aux mêmes valeurs spécifié dans les fichiers server_X.json. Ainsi, le premier serveur spécifié dans la liste correspond au serveur serveur_1.json.
Cela veut dire que vous devez avoir au moins le même nombre de fichier server_x.json que de serveur présents dans la liste des serveurs difinit dans dispatcher_configs.json. Un maximum de 4 serveurs est fixé.
Vous pouvez aussi spécifier le facteur de capacité. Il s'agit du nombre d'opération envoyé au serveur en plus de sa capacité (ie n = C + fC);

*LDAP*
dans LDAP_configs.json
Vous pouvez spécifier le host et le port de ce processus.

*Dans tous les cas, le port du RMIRegistry doit être 5005, il n'est pas configurable nul part*

Faites vos configuration comme vous le désirez puis exécuter les étapes ci-dessous

Étapes:

1. ant
2. cd bin/
3. rmiregistry 5005 & 
4. cd ..
5. ./LDAP.sh
6. Lancé autant de serveur que vous le désirez. Pour lancer un serveur il faut faire la commande :
	./OperationServer X
	X correspond au serveur voulu exemple serveur_1.json
7. ./client.sh

Il est possible d'arrêter et de repartir un sserveur en cours de route. Vous devriez voir dans les terminaux approprié qu'une erreur a été lancé, mais que le processus termine tout de même ses tâches.
