import csv

addresses = []
patients = []
with open('C:/Users/Hayden Taylor/Downloads/regions.csv', 'rt', encoding="utf8") as csvfile:
        spamreader = csv.reader(csvfile, delimiter=',', quotechar='|')
        for row in spamreader:
            addresses.append(row)
i = 1 # ignore header
with open('C:/Users/Hayden Taylor/Downloads/testCSV2.csv', 'rt', encoding="utf8") as csvfile:
        spamreader = csv.reader(csvfile, delimiter=',', quotechar='|')
        header = next(spamreader, None) #skip header
        patients.append(header)
        for row in spamreader:
            try:
                row[10] = addresses[i][0].split(" ")[0] #street no
                row[11] = ' '.join(addresses[i][0].split(" ")) #street address
                row[14] = addresses[i][3] #suburb
                row[13] = addresses[i][1] #city
                row[15] = addresses[i][2] #zip
                patients.append(row)
            except IndexError:
                i = 0
            i += 1

with open('C:/Users/Hayden Taylor/Downloads/patients.csv', 'w', encoding="utf8") as myfile:
    wr = csv.writer(myfile, quoting=csv.QUOTE_ALL)
    for patient in patients:
        wr.writerow(patient)
print(patients[0:10])

