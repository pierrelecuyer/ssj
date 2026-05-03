# Instructions pour développer la librairie SSJ

Ce document donne des instructions pour:

1. Obtenir le code de SSJ et compiler la librairie à partir du code source.

2. Mettre à jour le dépôt public de SSJ sur GitHub.

3. Créer une nouvelle version de SSJ, et l'envoyer sur le dépôt [Maven Central](https://search.maven.org).

4. Mettre à jour la documentation sur GitHub.

Si on veut seulement utiliser SSJ (pas le modifier), il suffit de
suivre les [instructions d'installation](https://github.com/umontreal-simul/ssj#installation).

==============================================================================================

## 1. Obtenir le code source et compiler SSJ

On récupère le code source depuis le [dépôt GitHub de SSJ](https://github.com/umontreal-simul/ssj):

    git clone https://github.com/umontreal-simul/ssj.git

Ceci crée un nouveau repertoire `ssj` dans votre repertoire courant, qui contiendra le dernier commit de la branche `master` du code source de SSJ. On peut plutôt cloner une branche spécifique en ajoutant l'option `-b branche` où `branche` est le nom de la branche sur GitHub. Par exemple, pour cloner seulement la branche `develop`:

    git clone -b develop https://github.com/umontreal-simul/ssj.git


### Ajouter la branche `develop` pour faire du développement

Pour ajouter de nouvelles fonctionnalités dans SSJ, il est préférable de travailler à partir de la branche `develop` (ou une sous-branche). Si vous avez déjà téléchargé SSJ, pour y ajouter la  branche `develop`, allez dans votre répertoire de SSJ et exécutez:

    git fetch
    git checkout develop
    
Le contenu du répertoire `ssj` devrait maintenant refléter la branche `develop`. L'opération `git branch` permet de vérifier cela. Pour retourner à la branch `master`: 

    git checkout master

### Créer une nouvelle branche git

Pour plus de sûreté lorsqu'on fait de changements ou essais, il est recommandé de créer une sous-branche de `develop`, puis de faire et tester les changements dans cette branche. On pourra fusionner cette sous-branche avec `develop` après avoir bien testé les changements. Pour créer cette nouvelle branche et y aller, faire:

    git checkout develop
    git branch nouvelle_branche
    git checkout nouvelle_branche
    
Les deux dernières instructions peuvent être aussi remplacées par

    git checkout -b nouvelle_branche

### Changer de branche git

Lorsqu'on change de branche, git modifiera le contenu du répertoire `ssj` pour refléter la branche choisie. Mais avant de changer de branche, il faut s'assurer d'enregistrer les changements au dépôt local via la commande `git commit`. Pour passer à la branche `autre_branche`, faire:

    git checkout autre_branche

La commande `git branch` permet de voir toutes les branches existantes du dépôt local.


### <a name="compilation"></a> Compilation

Notez que dans le cas où votre machine est connectée à un réseau, il est possible de compiler une version de SSJ qui se trouve ailleurs sur le réseau, mais il est beaucoup plus rapide de compiler une version qui se trouve sur le disque local de votre machine.

On peut compiler SSJ et l'installer dans le dépôt Maven local en se plaçant dans le répertoire `ssj` et avec la commande:

    ./gradlew \
        -PbuildDocs \
        -PcrossCompile \
        -Pssjutil.jni.build \
        -Prandvar.jni.build \
        -Punuran.prefix.win32=/u/simul/opt/unuran-mingw32 \
        -Punuran.prefix=/u/simul/opt/unuran \
        publishToMavenLocal

Pour éviter d'avoir à passer tous ces paramètres en ligne de commande, on peut
alternativement ajouter les lignes suivantes dans le fichier `gradle.properties`:

    buildDocs
    crossCompile
    ssjutil.jni.build
    randvar.jni.build
    unuran.prefix.win32=/u/simul/opt/unuran-mingw32
    unuran.prefix=/u/simul/opt/unuran

et la commande Gradle ci-haut se réduit à:

    ./gradlew publishToMavenLocal

Dans le reste de ce guide, on suppose que les options sont données dans le fichier `gradle.properties`.  Dans le cas contraire, il faut toujours ajouter toutes les options `-P…` à la commande `./gradlew`.

L'option `buildDocs` nécessite [Doxygen](http://www.stack.nl/~dimitri/doxygen/). Les options `ssjutil.jni.build` et `randvar.jni.build` nécessitent un compilateur C et servent à compiler les JNI, dont le chronomètre fourni par `ssjutil` et l'interface avec UNU.RAN fournie par `randvar`. L'option `crossCompile` nécessite un compilateur C qui peut générer du code Windows et produit les DLL pour l'utilisation des JNI sous Windows. Par défaut, le fichier `build.gradle` est configuré pour utiliser le compilateur `x86_64-w64-mingw32-gcc` de [MinGW-w64](https://sourceforge.net/projects/mingw-w64/) pour effectuer la cross-compilation (il faut s'assurer que MinGW-w64 soit installé pour utiliser ce compilateur). L'utilisateur peut cependant spécifier un autre compilateur. Toutes ces options sont facultatives.

Si la tâche `publishToMavenLocal` de Gradle s'exécute avec succès, SSJ se trouvera installé dans le dépôt Maven local de l'utilisateur, normalement dans le répertoire `$HOME/.m2/repository`.

On peut aussi lancer ces commandes à partir du compte `simul` et indiquer à un utilisateur au labo d'ajouter le dépôt Maven `/u/simul/.m2/repository` du compte `simul` à la liste de dépôts Maven de son projet Maven. Il faut s'assurer que l'utilisateur ait les permissions suffisantes pour accéder à ce répertoire et à son contenu.

---

## 2. Mettre à jour le dépôt *GitHub*

Si on prévoit mettre à jour le dépôt GitHub, il faut avoir un compte GitHub, et on recommande fortement de faire d'abord un *fork* du projet SSJ dans son propre compte GitHub, puis de cloner cette copie dans un dépôt local pour y faire les changements, et ensuite faire un commit puis un *push* vers son dépôt GitHub personnel. Pour cloner dans le dépôt local, on fera:

    git clone git@github.com:umontreal-simul/ssj.git

On peut ensuite faire une requête pour demander d'ajouter les changements au dépôt officiel via le bouton *New pull request*. Les changements doivent être approuvés avant d'être incorporés dans le dépôt officiel de SSJ. 

Les utilisateurs *membres* du groupe [umontreal-simul](https://github.com/umontreal-simul)
peuvent court-circuiter cette demande d'approbation et envoyer leurs mises à jour directement de leur dépôt local vers leur dépôt personnel puis vers le dépôt public GitHub à l'aide de la commande *push*:

    git push nom_remote nom_branche
    
où `nom_remote` est le nom du remote (endroit) et `nom_branche` est le nom de la branche à mettre à jour.
Par exemple, pour effectuer les mises à jour dans la branche `develop`, il faut exécuter:

    git push origin develop

Dans ces exemples, on suppose que `origin` est un *remote* qui pointe vers le dépôt GitHub. Si on exécute seulement `git push`, alors git enverra les mises à jour aux *remote* et *branch* choisis par défaut.

Pour vérifier que le remote `origin` pointe bien vers GitHub, on peut exécuter la commande:

    git remote show origin

---

## 3. Créer une nouvelle version de SSJ

Pour créer une nouvelle version de SSJ, il faut: 

1. Modifier la ligne `version = '…'` dans le fichier `build.gradle` pour indiquer la nouvelle version à distribuer, 

2. Exécuter la  commande de compilation Gradle.

    ./gradlew publishToMavenLocal

3. Nous conseillons également de créer un *tag* pour identifier la nouvelle version dans l'historique 
des commits de git:

    git tag -a version -m "Message"
    
où `version` est l'identifiant de la version (e.g., `v3.3.0`), et `"Message"` est le message du commit. Par exemple, la commande suivante:

    git tag -a v3.3.0 -m "Update SSJ to version 3.3.0"

La commande `git tag` affiche la liste des tags. Le *tag* que nous venons de créer, sera utilisé pour créer une nouvelle distribution (*release*) sur GitHub. La prochaine section explique comment créer une distribution sur GitHub.

**Numérotation des versions.** Nous utilisons la convention suivante pour numéroter les versions de SSJ. L'identifiant d'une version est composé de trois nombres `X.Y.Z` tels que

- `X` représente un changement majeur qui peut briser la compatibilité avec les versions précédentes.

- `Y` représente un changement mineur, e.g.: ajout de nouveaux packages ou classes.

- `Z` représente un changement minime ou un *patch*; généralement pour des corrections de bogues.

---

### Créer une nouvelle distribution (*release*) sur GitHub

À chaque nouvelle version de SSJ, nous créons une nouvelle distribution sur GitHub:

1. Faire un *push* du tag vers GitHub:

    git push origin nom_tag

où `nom_tag` est le nom du tag que nous venons de créer ci-haut. Exécuter la commande `git tag` pour voir la liste des tags. Par exemple: 

    git push origin v3.3.0

Aprés l'execution de la commande précedente, GitHub se charge de créer un nouvelle section pour la nouvelle version de SSJ.	Vérifier sur [dépôt GitHub de SSJ](https://github.com/umontreal-simul/ssj/tags) que le tag est bien présent dans la liste. 
     
2. Nous pouvons maintenant créer une nouvelle distribution (*release*) pour décrire la nouvelle version
et ajouter(par un upload) les fichiers de distribution binaires à la *release*. Les fichiers de distribution se trouvent dans le répertoire `build/distributions`, où on trouvera 2 fichiers compressés avec les formats `zip` et `tar.bz2`. Il faut que l'option `buildDocs` soit active (vérifier que la ligne n'est pas commentée) dans le fichier `gradle.properties` lors de la compilation de SSJ ou lors de la création des fichiers de distribution. Si les fichiers de distribution ne sont pas présents ou si nous voulons les recréer, on peut le faire via:

    ./gradlew distZip
    ./gradlew distTar

pour créer les fichiers `zip` et `tar.bz2`, respectivement.

3. Sur la page [GitHub de SSJ](https://github.com/umontreal-simul/ssj/releases/), on peut voir l'historique des `releases` de SSJ. On peut éditer (en cliquant sur le bouton `edit`) un `release` pour voir les informations à fournir pour en créer un nouveau. On peut faire cela en cliquant respectivement sur les boutons `Releases` et `Draft a new release`. Indiquer la version du `release` (par exemple v3.3.0), donnez-en une description, et faites un `upload` des fichiers compressés dans les formats `zip` et `tar.bz2` qui se trouvent dans le repertoire `build/distributions`.

---

### Mettre à jour le dépôt *Maven Central*

Pour charger une nouvelle version de SSJ dans le [dépôt Maven Central](http://search.maven.org/), il faut
disposer d'un compte Jira sur Sonatype et avoir les privilèges de télécharger pour le groupe `ca.umontreal.iro.simul` sur Sonatype Jira. Si vous n'avez pas un tel compte Jira, la premiere étape est
d'en ouvrir un via le site [Sonatype JIRA](https://issues.sonatype.org/). Branchez-vous ensuite sur ce compte puis créez une requête (an `issue`), via le bouton **Create**, pour demander la permission de télécharger pour le groupe `ca.umontreal.iro.simul`. Lors de la création de la requête, il faut choisir *Community Support - Open Source Project Repository Hosting*, décrire la requête dans le champ *Description*, et ajouter une référence à l'ancienne requête [https://issues.sonatype.org/browse/OSSRH-18412](https://issues.sonatype.org/browse/OSSRH-18412). Les autres informations nécessaires sont:

Type: Task

Group id:  ca.umontreal.iro.simul

Project URL:  https://github.com/umontreal-simul/ssj

SCM url:  https://github.com/umontreal-simul/ssj.git 

La permission sera accordée par un administrateur (cela peut prendre quelques minutes ou quelques heures). Il se pourrait que l'administrateur demande des preuves d'appartenance au groupe. L'authentification peut être facilitée si on crée le compte Sonatype avec la même adresse courriel que votre compte GitHub. Vous ne pouvez pas passer aux prochaines étapes tant que la permission de télécharger pour le groupe `ca.umontreal.iro.simul` n'est pas accordée.

L'une des conditions de Sonatype pour publier vos artefacts (.jar) dans le dépôt Maven Central est qu'ils
aient été signés avec [GPG](https://central.sonatype.org/publish/requirements/gpg/). GPG est une implémentation librement disponible de la norme OpenPGP qui vous offre la possibilité de générer une signature, de gérer des clés et de vérifier des signatures. Il faut une clé générée par GPG pour signer et envoyer les fichiers (les .jar) de la nouvelle version de SSJ dans le repertoire Maven Central. Pour vérifier si GPG est déja installé dans votre environnement de travail, faites:

    gpg --version

qui devrait vous donner quelque chose qui ressemble à 

    thiongam@pomelo ~ $ gpg --version
    gpg (GnuPG) 2.2.35
    libgcrypt 1.9.4-unknown
    Copyright (C) 2022 g10 Code GmbH
    License GNU GPL-3.0-or-later &lt;https://gnu.org/licenses/gpl.html&gt;
    This is free software: you are free to change and redistribute it.
    There is NO WARRANTY, to the extent permitted by law.
    .... 

Sinon, téléchargez et installez [GnuPG](https//www.gnupg.org/download/).

Générer ensuite une paire de clés avec la commande:

    gpg --gen-key

Saisissez lorsqu'on vous le demande votre nom au complet, votre adresse électronique, et votre mot de passe GPG, `Pass Phrase`. Retenez bien le mot de passe choisi. La durée de validité de la clé est fixée par défaut à 2 ans. Si la clé est expirée, vous pouvez la prolonger, à condition de posséder la clé et connaître la `Pass Phrase`.
Lorsque la clé sera générée, vous verrez quelque chose qui ressemble à:

    $ gpg --gen-key
    gpg (GnuPG) 2.2.19; Copyright (C) 2019 Free Software Foundation, Inc.
    This is free software: you are free to change and redistribute it.
    There is NO WARRANTY, to the extent permitted by law.
    Note: Use "gpg --full-generate-key" for a full featured key generation dialog.
    GnuPG needs to construct a user ID to identify your key.
    Real name: Central Repo Test
    Email address: central@example.com
    You selected this USER-ID:
    "Central Repo Test &lt;central@example.com&gt;"
    ....
    pub   rsa3072 2021-06-23 [SC] [expires: 2023-06-23]
          CA925CD6C9E8D064FF05B4728190C4130ABA0F98
    uid                      Central Repo Test &lt;central@example.com&gt;
    sub   rsa3072 2021-06-23 [E] [expires: 2023-06-23]

Une fois la paire de clés générée, vous pouvez voir les clés à l'aide de la commande:

    gpg --list-keys

qui donnera quelque chose qui ressemble à

    $ gpg --list-keys
    /home/mylocaluser/.gnupg/pubring.kbx
    ---------------------------------
    pub   rsa3072 2021-06-23 [SC] [expires: 2023-06-23]
          CA925CD6C9E8D064FF05B4728190C4130ABA0F98
    uid           [ultimate] Central Repo Test &lt;central@example.com&gt;
    sub   rsa3072 2021-06-23 [E] [expires: 2023-06-23]

La ligne commençant par `pub` indique la taille (rsa3072), et donne la clé `keyid` (CA925CD6C9E8D064FF05B4728190C4130ABA0F98). Les 8 derniers caractères de `keyid` représentent le `shorId` de la clé, qui est (0ABA0F98) dans cet exemple.

Il faut maintenant exporter les clés secretes GPG dans votre repertoire courant par la commande:

    gpg --export-secret-keys &gt; ~/.gnupg/secring.gpg 

La prochaine étape consiste à distribuer votre clé publique:

    gpg --keyserver keyserver.ubuntu.com --send-keys CA925CD6C9E8D064FF05B4728190C4130ABA0F98

Vous pouvez maintenant ajouter au fichier `gradle.properties` les 5 lignes suivantes, qui vont permettre de signer les fichiers à télécharger:

    signing.keyId=shortId
    signing.password=passPhrase 
    signing.secretKeyRingFile=/$HOME/.gnupg/secring.gpg
    ossrhUsername=loginSonatype
    ossrhPassword=passWordSonatype

où `shortId` est le `shortId` de votre clé, `passPhrase` est votre mot de passe GPG, `loginSonatype` est le nom d'usager de votre compte Sonatype, `passWordSonatype` est votre mot de passe Sonatype, et `$HOME` est le nom du répertoire où `.gnupg/secring.gpg` se trouve.

Important: ne modifiez pas le fichier `gradle.properties` du projet qui sera publié sur Git par un `commit`. 
Vous devez modifier celui trouve dans le repertoire `~/.gradle/`.

Vous pouvez ensuite publier dans votre Maven local via:

    export OSSRH_USER=...
    export OSSRH_PASS=...
    ./gradlew publishToMavenLocal

en affectant à `OSSRH_USER` votre login Jira Sonatype et à `OSSRH_PASS` votre mot de passe Jira Sonatype. Si ceci s'excute avec succès, vous pouvez maintenant publier sur Maven Central la nouvelle version de SSJ par la commande:

    ./gradlew publish         

Vous pourrez ensuite visualiser la nouvelle version sur [Sonatype Nexus](https://oss.sonatype.org/).
Pour vous y connecter, utilisez votre login et mot de passe Jira Sonatype. Cliquez ensuite sur `Staging Repositories` pour voir votre nouvelle version de SSJ. Vous pouvez la choisir et cliquer sur `content` pour vérifier que tout y est, .jar, doc, etc., puis `close`, qui peut prendre quelques minutes à s'exécuter. Une fois le statut devenu `CLOSE`, vous pouvez cliquer sur `RELEASE` puis `CONFIRM` pour lancer cette nouvelle version. Cela peut prendre quelques heures pour que cette nouvelle version soit disponible au public sur le site [Maven Central](http://search.maven.org/). Vous avez aussi la possibilité de la supprimer via `DROP`. 

---

## 4. Mettre à jour la documentation sur GitHub

La page de [documentation de SSJ](http://umontreal-simul.github.io/ssj/) sur GitHub est générée à partir de la branche `gh-pages`. Mettre à jour la documentation revient à modifier le contenu de cette branche. La branche `gh-pages` est spéciale, car elle ne contient que les fichiers du site HTML; il n'y a aucun code source.

Voici les differentes étapes à suivre pour mettre à jour la documentation.

1. cloner la branche `gh-pages` dans un autre répertoire local (par exemple dans le répertoire `ssj-doc`) avec la commande suivante:

    git clone -b gh-pages git@github.com/umontreal-simul/ssj.git 

La documentations se trouve maintenant dans le repertoire nommé `ssj-doc/ssj/docs/master`

2. Placez-vous maintenant dans votre répertoire `ssj` de développement (qui contient le code source). La commande `git branch`, montrera que vous êtes bien placé dans votre branche local de développement. Mettez à jour la documentation Doxygen en exécutant la tâche `buildDocs` avec gradle (cette tâche est incluse dans la [commande de compilation ci-haut](#compilation)).

3. Copier tous les fichiers générés dans le répertoire `ssj/build/docs/html` vers le répertoire de documentation de la branch `gh-pages` qui est `ssj-doc/ssj/docs/master`.

4. Mettre à jour la branche `gh-pages` sur GitHub avec les commandes:

    git add -A
    git commit "Documentation de la version 3.3.2 de SSJ"
    git push

Cette commande suppose que la nouvelle version est 3.2.2 et que le *remote* par défaut est celui de SSJ sur GitHub. Autrement, il faut ajouter le nom du remote: `git push nom_du_remote`.

---

*La version HTML de ce guide a été générée à partir d'un .md avec [Markdown:
Daring Fireball Dingus](http://daringfireball.net/projects/markdown/dingus) le 2 mai 2026*

<style type="text/css">
p, h1, h2, h3, h4, h5 { font-family: sans-serif; }
pre { padding: 1.5ex 3em; }
pre, code { background-color: #eee; }
p > code { border: solid #ddd 1px; padding: 1px; border-radius: 3px; }
</style>

