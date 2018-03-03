# CLI Donor View Manual Test

1. Add a donor using `donor add --ird=1234 -f=Andrew -l=S -b=9999-09-09`
2. Attempt to update the donor using `donor update -s=1234 --bloodgroup=A_POSITIVE --gender=female --height=179.8 --ird=12345 --region=northland --street1="15Brunswick" --street2="emptinessiess" --suburb=huntsbury --weight=120.4 --zip=8080 --dateofbirth=1999-09-09 --dateofdeath=1999-09-10 --firstname=Hayden --lastname=poo --middlenames="hey,bro,sup"`
3. Attempt to view removed donor using 'donor view --ird=1234`. It should fail because the ird has changed to 12345 from step 2.
4. View updated donor using `donor view --ird=12345`