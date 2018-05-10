# CLI Donor Donations Manual Test

1. Add a donor using `donor add --ird=1234 -f=Andrew -l=S -b=9999-09-09`
2. Attempt to add donations to the donor using `donor donations -i=1234 --add=liver,lung`
3. Ensure the donor has the organs added using `donor view --ird=1234`