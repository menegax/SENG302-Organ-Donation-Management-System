# CLI Donor Update Manual Test

1. Add a patient using `patient add --ird=1234 -f=Andrew -l=S -b=9999-09-09`
2. Attempt to update the patient using `patient update -s=1234 --bloodgroup=A_POSITIVE --gender=female --height=179.8 --ird=12345 --region=northland --streetNumber="15Brunswick" --street2="emptinessiess" --suburb=huntsbury --weight=120.4 --zip=8080 --dateofbirth=1999-09-09 --dateofdeath=1999-09-10 --firstname=Hayden --lastname=poo --middlenames="hey,bro,sup"`
3. Attempt to view removed patient using 'patient view --ird=1234`. It should fail because the ird has changed to 12345 from step 2.
4. View updated patient using `patient view --ird=12345`

**ADD MOST RECENT RESULTS TO GITLAB TEST SUITE SUMMARY**
