# GovTech Hackathon 2023 - Dashboard Ladestationen Elektroautos
## Einleitung
Die Elektromobilität gewinnt immer mehr an Bedeutung und Elektrofahrzeuge werden zunehmend auf unseren Straßen zu sehen sein. Um den steigenden Bedarf an Ladestationen für Elektrofahrzeuge zu decken und eine effiziente Nutzung dieser Ladestationen zu gewährleisten, ist es wichtig, den Stromverbrauch und Konsum in Echtzeit zu überwachen. Aus diesem Grund wurde im Rahmen einer Challenge das Projekt "Dashboard Ladestationen Elektroautos" ins Leben gerufen, bei dem ein benutzerfreundliches und intuitives Dashboard entwickelt wurde, das die Überwachung des Stromverbrauchs und Konsums bei Ladestationen für Elektroautos ermöglicht. In diesem Projekt wurde die bereits vorhandene Datenquelle www.ich-tanke-strom.ch genutzt, um den aktuellen Status und die Verfügbarkeit der Ladestationen in Echtzeit zu verfolgen. Dieses Projekt bietet eine Möglichkeit für Netzbetreiber, Behörden und Stromanbieter, den Stromverbrauch und Konsum von Elektroautos bei Ladestationen in Echtzeit zu überwachen und somit eine effektive Planung der Netzauslastung zu gewährleisten.

## Architektur
Dashboard Ladestationen Elektroautos ist eine Online-Plattform, die eine umfassende Datenbank von öffentlichen Ladestationen für Elektrofahrzeuge in der Schweiz bereitstellt. Die Architektur der Plattform umfasst mehrere Komponenten und Technologien.

Die Plattform basiert auf einem Server-Client-Modell, bei dem die Serverseite die Datenbank von Ladestationen verwaltet und die Clientseite die Benutzeroberfläche für  das dashboard  bereitstellt. Die Serverseite wird durch eine API-Schnittstelle bereitgestellt, die von der Clientseite aufgerufen wird.

Die Datenbank der Ladestationen wird von ELASTIC bereitgestellt und durchsucht, um relevante Ergebnisse für die Benutzer zu liefern. Die Plattform nutzt auch DDEV, ein Tool zur Entwicklungsumgebung, um die Entwicklungsumgebung schnell und einfach einzurichten.

Die Benutzeroberfläche der Plattform wurde mithilfe von ReactJS und Openlayers entwickelt, um interaktive Karten und Benutzeroberflächen zu erstellen. Die Plattform ist auch in Craft CMS eingebettet, einem Content-Management-System, das es ermöglicht, Inhalte auf der Plattform einfach zu verwalten.

Insgesamt ist die Architektur von Dashboard Ladestationen Elektroauto darauf ausgelegt, eine einfache, benutzerfreundliche Plattform bereitzustellen, die es Benutzern ermöglicht, schnell und einfach Ladelast öffentliche Ladestationen für Elektrofahrzeuge in der Schweiz darzustellen.

## API/Schnittstelle

BEISPIEL
Das Backend stellt folgende Endpunkte zur Verfügung:

    /api/charging-points: gibt eine Liste aller verfügbaren Ladestationen zurück
    /api/charging-points/:id/status: gibt den Status (verfügbar, besetzt, defekt) einer bestimmten Ladestation zurück
    /api/charging-points/:id/history: gibt den historischen Stromverbrauch einer bestimmten Ladestation zurück
    
    
## Datenquellen 

Die Daten werden von den folgenden JSON-APIs bereitgestellt:

    https://data.geo.admin.ch/ch.bfe.ladestellen-elektromobilitaet/data/ch.bfe.ladestellen-elektromobilitaet.json: statische Daten zu den Ladestationen, aktualisiert kontinuierlich
    https://data.geo.admin.ch/ch.bfe.ladestellen-elektromobilitaet/status/ch.bfe.ladestellen-elektromobilitaet.json: Verfügbarkeitsdaten der Ladestationen, aktualisiert kontinuierlich
    https://data.geo.admin.ch/ch.bfe.ladestellen-elektromobilitaet/data/ch.bfe.ladestellen-elektromobilitaet_de.json: GeoJSON, aggregierte Daten der Ladestationen nach Standort
    

## Datenverarbeitung

Die Daten werden von Python-Skripten verarbeitet, um sie aus den APIs zu sammeln, bereinigen, aggregieren und in eine ELSATIC  zu speichern.

## Benutzeroberfläche
Das Dashboard zeigt eine Karte mit den verfügbaren Ladestationen und deren aktuellen Status (verfügbar, besetzt, defekt) sowie Diagramme des historischen Stromverbrauchs jeder Ladestation.


## Installation: 
Eine Anleitung zur Installation und Konfiguration des Systems auf einem lokalen Server oder in der Cloud.

## Anwendungsbeispiele:
Einige Beispiele dafür, wie das System zur Überwachung des Stromverbrauchs und Konsums bei Elektrofahrzeug-Ladestationen in der Schweiz verwendet werden kann.

## Limitationen: 
Eine Diskussion über die Grenzen des Systems, einschließlich möglicher Einschränkungen bei der Verwendung der verfügbaren Daten und der Genauigkeit der Ergebnisse.

## Zukünftige Entwicklungen: 
Eine Beschreibung möglicher zukünftiger Entwicklungen des Systems, einschließlich der Integration neuer Datenquellen und der Verbesserung der Benutzererfahrung.

## Links:

https://hack.opendata.ch/project/944

![image](Architektur.jpg)
