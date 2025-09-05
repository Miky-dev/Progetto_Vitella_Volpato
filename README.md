# 📌 Bacheca Annunci - Progetto Java

![Java](https://img.shields.io/badge/Java-21-orange)
![Swing](https://img.shields.io/badge/Swing-GUI-blue)
![JUnit](https://img.shields.io/badge/JUnit-5.0-green)

Bacheca Annunci è un'applicazione Java per la gestione di una bacheca di annunci di vendita e acquisto. Supporta sia un'interfaccia grafica sia un'interfaccia a riga di comando; i dati vengono persistiti su file di testo.

## 📝 Descrizione

L'applicazione permette agli utenti di:

- registrarsi e autenticarsi (gestione base utente),
- creare, modificare e rimuovere annunci,
- cercare annunci per parole chiave,
- rimuovere automaticamente gli annunci scaduti,
- salvare/caricare gli annunci su file (persistenza semplice, testuale).

## ✨ Funzionalità principali

- 👤 **Gestione utenti**: Registrazione con validazione base di email e nome utente
- 📢 **Gestione annunci**: Creazione con titolo, descrizione, prezzo, data di scadenza, autore e parole chiave; rimozione e visualizzazione di annunci.
- 🔍 **Ricerca intelligente**: Ricerca che restituisce annunci contenenti la parola cercata nel titolo, nella descrizione o tra le parole chiave.
- 📆 **Gestione scadenze**: Rimozione degli annunci scaduti tramite metodo dedicato nella Bacheca.
- 🔄 **Persistenza dei dati**: Salvataggio/caricamento su file di testo (annunci.txt) tramite la Bacheca.
- 💡 **Suggerimenti intelligenti**: Raccomandazioni di annunci di vendita in base alle ricerche di acquisto
- 💡 **Test automatici**: test JUnit 5 per Utente, Annuncio e Bacheca.

## 🏗️ Architettura

Il progetto segue il pattern architetturale **Model-View-Controller (MVC)**:

- **Model**: Le classi che rappresentano i dati e la logica di business: Annuncio, Bacheca, Utente.
- **View**: Le classi che gestiscono l'interfaccia utente
- **Controller**: Le classi che coordinano le interazioni tra model e view

## 📂 Struttura del progetto

```
Progetto_Vitella_Volpato/
src/
├── icon/
├── interfaccia/
│   ├── grafica/
│   │   ├── InterfacciaGrafica.java
│   │   ├── controllo/
│   │   │   └── ControlloBacheca.java
│   │   └── vista/
│   │       ├── BachecaPanel.java
│   │       ├── ContentPanel.java
│   │       ├── OpsPanel.java
│   │       └── UtentePanel.java
│   └── rigaDiComando/
│       └── InterfacciaRigaDiComando.java
├── main/
│   └── Main.java
├── modello/
│   ├── Annuncio.java
│   ├── Bacheca.java
│   ├── Utente.java
│   ├── exception/
│   │      ├── AnnuncioException.java
│   │      ├── AutoreNonAutorizzatoException.java
│   │      ├── BachecaException.java
│   │      └── UtenteException.java
│   │
│   ├── test/
│   │      ├── AnnuncioTest.java
│   │      ├── BachecaTest.java
│   │      ├── tets.txt
│   │      └── UtenteTest.java
│   │
│   └── annunci.txt
│
├── .classpath
├── Progetto_Vitella_Volpato.iml
└── Javadoc.md

```

## 🔧 Requisiti

- Java Development Kit (JDK) 21 o superiore
- JUnit 5 (per eseguire i test)
- IDE con supporto per Java (Eclipse, IntelliJ IDEA, VisualStudio, ecc.)

## 🚀 Installazione e avvio

1. Clona il repository:

```
https://github.com/Miky-dev/Progetto_Vitella_Volpato.git
```

2. Importa il progetto nel tuo IDE preferito come progetto Java esistente

3. Esegui la classe Main.java
   L'applicazione mostrerà un menu per scegliere tra:
   
    - Interfaccia grafica
    - Interfaccia a riga di comando
    - Uscita dal programma

## 💻 Utilizzo

### Interfaccia grafica

1. Dopo il login, verrai accolto dalla schermata principale con:

- Una sezione superiore con i pulsanti delle operazioni
- Una sezione scrollabile centrale che mostra gli annunci esistenti
- Una sezione inferiore che mostra le informazioni dell'utente loggato; nome e mail.

2. Operazioni principali:

- Aggiungere un nuovo annuncio: apre una form per titolo, descrizione, prezzo, scadenza e parole chiave.
- Rimuovere un annuncio esistente (se autorizzato).
- Cercare annunci per parole chiave.
- Pulire la bacheca dagli annunci scaduti.
- Aggiunta di parole chiave a un annuncio esistente (se autorizzato).

### Interfaccia a riga di comando

1. Dopo il login, verrà mostrato un menu con le operazioni disponibili:

- Aggiungi annuncio
- Rimuovi annuncio
- Cerca annuncio
- Pulisci bacheca
- Visualizza bacheca
- Aggiungi parola chiave ad annuncio
- Esci

2. Seguire le istruzioni testuali per inserire i dati.

## 🧪 Test

Il progetto include test unitari completi implementati con JUnit 5 per:

- Validazione della classe `Utente`
- Validazione della classe `Annuncio`
- Validazione della classe `Bacheca` e delle sue operazioni

Per eseguire i test, utilizza la funzionalità di test del tuo IDE o esegui i test JUnit direttamente.

---
Nella relazione tratteremo tutte le classi nello specifico.
