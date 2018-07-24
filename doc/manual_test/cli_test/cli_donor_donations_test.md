# CLI Donor Donations Manual Test

1. Add a patient using `patient add --ird=1234 -f=Andrew -l=S -b=9999-09-09`
2. Attempt to add donations to the patient using `patient donations -i=1234 --add=liver,lung`
3. Ensure the patient has the organs added using `patient view --ird=1234`