import pandas as pd
import numpy as np
import matplotlib.pyplot as plt

# Percorsi dei file CSV
sent_file_path = "messages_timestamp_log.csv"  # File con 'sentTimestamp'
received_file_path = "messages_log.csv"   # File con 'receivedTimestamp' e 'finalTimestamp'

if __name__ == "__main__":
    # Carica i due CSV
    df_sent = pd.read_csv(sent_file_path)
    df_received = pd.read_csv(received_file_path)

    # Unisce i due DataFrame
    df = pd.concat([df_sent.reset_index(drop=True), df_received.reset_index(drop=True)], axis=1)

    # Converti i Unix timestamp in formato datetime
    df['sentTimestamp'] = pd.to_datetime(df['sentTimestamp'], unit='ms')
    df['receivedTimestamp'] = pd.to_datetime(df['receivedTimestamp'], unit='ms')
    df['finalTimestamp'] = pd.to_datetime(df['finalTimestamp'], unit='ms')

    # Calcola le latenze
    df['from_asset_to_asp'] = (df['receivedTimestamp'] - df['sentTimestamp']).dt.total_seconds()
    df['from_asp_to_asset'] = (df['finalTimestamp'] - df['receivedTimestamp']).dt.total_seconds()
    df['e2e'] = (df['finalTimestamp'] - df['sentTimestamp']).dt.total_seconds()

    # Statistiche descrittive iniziali
    print("\nStatistiche descrittive (con outlier):")
    print(df[['from_asset_to_asp', 'from_asp_to_asset', 'e2e']].describe())

    # Individua la riga con il massimo valore di 'from_asset_to_asp'
    max_index = df['from_asset_to_asp'].idxmax()
    max_row = df.loc[max_index]
    print("\nRiga con il valore massimo di from_asset_to_asp:")
    print(max_row)

    # Creazione del primo boxplot (con outlier)
    plt.figure(figsize=(10, 6))
    df[['from_asset_to_asp', 'from_asp_to_asset', 'e2e']].plot(kind='box', vert=True, patch_artist=True)
    plt.title("Distribuzione dei Tempi di Latenza (con outlier)", fontsize=16)
    plt.xlabel("Categorie", fontsize=12)
    plt.ylabel("Tempo in secondi", fontsize=12)
    plt.savefig("boxplot.png")
    plt.show()

    # **Rimozione degli outlier con il metodo IQR**
    def remove_outliers(df, column):
        Q1 = df[column].quantile(0.25)
        Q3 = df[column].quantile(0.75)
        IQR = Q3 - Q1
        lower_bound = Q1 - 1.5 * IQR
        upper_bound = Q3 + 1.5 * IQR
        return df[(df[column] >= lower_bound) & (df[column] <= upper_bound)]

    # Rimozione outlier per ogni colonna di latenza
    df_clean = remove_outliers(df, 'from_asset_to_asp')
    df_clean = remove_outliers(df_clean, 'from_asp_to_asset')
    df_clean = remove_outliers(df_clean, 'e2e')

    # Nuove statistiche dopo la pulizia
    print("\nStatistiche descrittive (dopo rimozione outlier):")
    print(df_clean[['from_asset_to_asp', 'from_asp_to_asset', 'e2e']].describe())

    # Creazione del boxplot senza outlier
    plt.figure(figsize=(10, 6))
    df_clean[['from_asset_to_asp', 'from_asp_to_asset', 'e2e']].plot(kind='box', vert=True, patch_artist=True)
    plt.title("Distribuzione dei Tempi di Latenza (senza outlier)", fontsize=16)
    plt.xlabel("Categorie", fontsize=12)
    plt.ylabel("Tempo in secondi", fontsize=12)
    plt.savefig("boxplot_clean.png")
    plt.show()