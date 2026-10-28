# Aufgabenliste (Python, Android-tauglich)

Ein kleines Terminal-Programm zum Verwalten von Aufgaben. Es braucht nur
Python 3 – keine Zusatzpakete – und läuft daher direkt auf einem Android-Tablet.

## Auf dem Android-Tablet einrichten

Acode ist ein Editor; zum **Ausführen** von Python brauchst du zusätzlich
**Termux** (eine Linux-Konsole für Android).

1. **Termux installieren** – am besten aus **F-Droid** (die Play-Store-Version ist veraltet).
2. In Termux einmalig:
   ```sh
   pkg update && pkg upgrade
   pkg install python git
   termux-setup-storage      # Zugriff auf den Gerätespeicher erlauben
   ```
3. Repository holen (z. B. in den gemeinsamen Speicher, damit Acode es sieht):
   ```sh
   cd ~/storage/shared
   git clone https://github.com/danjack12345/test.git
   cd test
   ```
4. **In Acode öffnen:** Ordner öffnen → `Interner Speicher/test` → `aufgaben.py`.
5. **Starten** in Termux:
   ```sh
   cd ~/storage/shared/test
   python aufgaben.py
   ```

### Optional: direkt aus Acode starten

Mit dem Acode-Plugin **AcodeX – Terminal** (verbindet sich mit Termux) bekommst
du ein Terminal im Editor und kannst `python aufgaben.py` dort ausführen, ohne
die App zu wechseln. Die Einrichtung ist in der Plugin-Beschreibung erklärt.

## Bedienung

```
=== Aufgabenliste ===
  1) Aufgaben anzeigen
  2) Aufgabe hinzufügen
  3) Aufgabe abhaken / zurücksetzen
  4) Aufgabe löschen
  5) Erledigte entfernen
  q) Beenden
```

Die Aufgaben werden in `aufgaben.json` neben dem Skript gespeichert.

## Änderungen mit Claude

Beschreibe Claude einfach, was das Programm zusätzlich können soll; die
Änderungen landen auf diesem Branch. In Termux holst du sie mit:

```sh
git pull
```

---

# Android-App (Kotlin)

Im Ordner `android/` liegt dieselbe Aufgabenliste als echte Android-App
(Kotlin + Jetpack Compose).

## APK bekommen – ohne PC

Bei jedem Push nach `main`, der etwas in `android/` ändert, baut GitHub die APK
automatisch:

1. Im Browser auf GitHub das Repo öffnen → Reiter **Actions**.
2. Den obersten Lauf **„Android-APK bauen“** öffnen (grüner Haken = fertig).
3. Unten bei **Artifacts** auf **aufgaben-apk** tippen → lädt eine ZIP-Datei.
4. ZIP in der Dateien-App öffnen, `app-debug.apk` antippen und installieren
   (beim ersten Mal „Installation aus unbekannten Quellen“ für den Browser bzw.
   die Dateien-App erlauben).

Einen Build kannst du auch von Hand starten: Actions → „Android-APK bauen“ →
**Run workflow**.

Updates lassen sich direkt über die alte Version installieren, weil die APK
immer mit demselben Debug-Schlüssel (`android/app/debug.keystore`) signiert
wird. Dieser Schlüssel ist nur zum Testen gedacht, nicht für den Play Store.

## Wo ist was?

| Datei | Inhalt |
|---|---|
| `android/app/src/main/java/de/aufgaben/MainActivity.kt` | Der ganze App-Code: Oberfläche und Speichern |
| `android/app/src/main/res/values/strings.xml` | App-Name |
| `android/app/build.gradle.kts` | Build-Einstellungen, Version, Bibliotheken |
| `.github/workflows/android.yml` | Der automatische Build bei GitHub |

Schlägt ein Build fehl (rotes Kreuz bei Actions), steht die Fehlermeldung im
Log des Laufs – einfach an Claude weitergeben.
