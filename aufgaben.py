#!/usr/bin/env python3
"""Einfache Aufgabenliste für das Terminal.

Läuft mit reinem Python 3 (keine Zusatzpakete) – z. B. in Termux auf Android.
Die Aufgaben werden in einer JSON-Datei neben diesem Skript gespeichert.

Start:  python aufgaben.py
"""

import json
import os
from datetime import datetime

DATEI = os.path.join(os.path.dirname(os.path.abspath(__file__)), "aufgaben.json")


def laden():
    if not os.path.exists(DATEI):
        return []
    try:
        with open(DATEI, encoding="utf-8") as f:
            return json.load(f)
    except (json.JSONDecodeError, OSError):
        print("Warnung: Aufgabendatei konnte nicht gelesen werden, starte leer.")
        return []


def speichern(aufgaben):
    with open(DATEI, "w", encoding="utf-8") as f:
        json.dump(aufgaben, f, ensure_ascii=False, indent=2)


def anzeigen(aufgaben):
    if not aufgaben:
        print("\n  (keine Aufgaben)\n")
        return
    print()
    for i, a in enumerate(aufgaben, start=1):
        haken = "x" if a["erledigt"] else " "
        print(f"  {i:>2}. [{haken}] {a['text']}  ({a['erstellt']})")
    offen = sum(not a["erledigt"] for a in aufgaben)
    print(f"\n  {offen} offen, {len(aufgaben) - offen} erledigt\n")


def nummer_abfragen(aufgaben, frage):
    eingabe = input(frage).strip()
    if not eingabe.isdigit() or not 1 <= int(eingabe) <= len(aufgaben):
        print("Ungültige Nummer.")
        return None
    return int(eingabe) - 1


def hinzufuegen(aufgaben):
    text = input("Neue Aufgabe: ").strip()
    if not text:
        print("Leere Aufgabe ignoriert.")
        return
    aufgaben.append({
        "text": text,
        "erledigt": False,
        "erstellt": datetime.now().strftime("%d.%m.%Y %H:%M"),
    })
    speichern(aufgaben)
    print("Hinzugefügt.")


def umschalten(aufgaben):
    i = nummer_abfragen(aufgaben, "Welche Nummer abhaken/zurücksetzen? ")
    if i is not None:
        aufgaben[i]["erledigt"] = not aufgaben[i]["erledigt"]
        speichern(aufgaben)
        print("Status geändert.")


def loeschen(aufgaben):
    i = nummer_abfragen(aufgaben, "Welche Nummer löschen? ")
    if i is not None:
        entfernt = aufgaben.pop(i)
        speichern(aufgaben)
        print(f"Gelöscht: {entfernt['text']}")


def erledigte_entfernen(aufgaben):
    vorher = len(aufgaben)
    aufgaben[:] = [a for a in aufgaben if not a["erledigt"]]
    speichern(aufgaben)
    print(f"{vorher - len(aufgaben)} erledigte Aufgabe(n) entfernt.")


MENUE = {
    "1": ("Aufgaben anzeigen", anzeigen),
    "2": ("Aufgabe hinzufügen", hinzufuegen),
    "3": ("Aufgabe abhaken / zurücksetzen", umschalten),
    "4": ("Aufgabe löschen", loeschen),
    "5": ("Erledigte entfernen", erledigte_entfernen),
}


def main():
    aufgaben = laden()
    print("=== Aufgabenliste ===")
    while True:
        for taste, (name, _) in MENUE.items():
            print(f"  {taste}) {name}")
        print("  q) Beenden")
        wahl = input("> ").strip().lower()
        if wahl in ("q", "quit", "exit"):
            print("Tschüss!")
            break
        if wahl in MENUE:
            MENUE[wahl][1](aufgaben)
        else:
            print("Unbekannte Auswahl.")


if __name__ == "__main__":
    try:
        main()
    except (KeyboardInterrupt, EOFError):
        print("\nTschüss!")
