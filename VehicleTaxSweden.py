# A small script to calculate the new tax for road vehicles in Sweden
# Data is collected from the following address and is subject to change
# https://transportstyrelsen.se/sv/vagtrafik/Fordon/Fordonsskatt/Hur-bestams-skattens-storlek/
#
# Author: Joakim Ewenson <joakim@ewenson.se>
# Date: 2019-04-08

# Set up default values for the calculation
isBonusMalus = False
isEcoFuel = False
isDiesel = False
vehicleTax = 0

# Set up default components
basicFee = 360                  # swedish krona
carbonComponent = 22            # swedish krona
carbonComponentEco = 11         # swedish krona
carbonComponentLevel = 111      # gram / kilometer

# Set up bonus/malus components
malusComponentLow = 82          # swedish krona
malusComponentHigh = 107        # swedish krona
malusComponentLevelLow = 95     # gram / kilometer
malusComponentLevelHigh = 140   # gram / kilometer
malusModelYear = 2018           # year

# Set up diesel component
dieselComponentHigh = 500       # swedish krona
dieselComponentLow = 250        # swedish krona
dieselComponentLevel = 2008     # year
dieselFuelFactor = 13.52        # if newer than 1 july 2018, multiply co2 emission by this
dieselMultFactor = 2.37         # if older than 1 july 2018, multiply total tax by this

# Set up the calculation
def VehicleTaxCalculation(carbonOutput, modelYear, isBonusMalus, dieselFuel, ecoFuel):

    # Determine the basic fee as current vehicle tax
    vehicleTax = basicFee

    # Check if vehicle is under the bonus/malus system
    if (modelYear >= malusModelYear):

        # Check if Bonus/Malus-flag is set to true
        if (isBonusMalus == True):

            # Check if carbon output is over 140 grams per km
            if (carbonOutput > malusComponentLevelHigh):

                # Calculate the difference in grams carbon per km
                malusDifferenceHigh = carbonOutput - malusComponentLevelHigh
                malusDifferenceLow = (carbonOutput - malusDifferenceHigh) - malusComponentLevelLow

                # Calculate the cost of malus tax
                malusTaxHigh = malusDifferenceHigh * malusComponentHigh
                malusTaxLow = malusDifferenceLow * malusComponentLow

                vehicleTax += malusTaxHigh
                vehicleTax += malusTaxLow
            else:

                carbonDifference = carbonOutput - malusComponentLevelLow
                carbonFee = carbonDifference * malusComponentLow
                vehicleTax += carbonFee

        # Check if Bonus/Malus-flag is set to false
        if (isBonusMalus == False):

            vehicleTax += (carbonOutput - carbonComponentLevel) * carbonComponent

        # Check if eco fuel is checked and if so, calculate lower taxes
        if (isEcoFuel):

            vehicleTax += (carbonOutput - carbonComponentLevel) * carbonComponentEco

        # Check if diesel is used and if so, ad additional taxes
        if (dieselFuel):

            if (modelYear >= dieselComponentLevel):
                dieselComponent = dieselComponentLow

            else:
                dieselComponent = dieselComponentHigh

            vehicleTax += (carbonOutput * dieselFuelFactor) + dieselComponent
    else:
        # Check if carbon output is above current level
        carbonDifference = carbonOutput - carbonComponentLevel
        if (carbonDifference > 0):
            if (ecoFuel):
                carbonFee = carbonDifference * carbonComponentEco
            else:
                carbonFee = carbonDifference * carbonComponent
            vehicleTax += carbonFee

        # Check if diesel is used and if so, ad additional taxes
        if (dieselFuel):
            vehicleTax = (vehicleTax * dieselMultFactor) + dieselComponentLow

    # Check if calculated tax is below the basic fee
    if (vehicleTax < basicFee):
        vehicleTax = basicFee

    # Return output
    return int(vehicleTax)

# Get user input
carbonInput = int(input("Antal gram koldioxid per km: "))
modelYearInput = int(input("Bilens årsmodell: "))
bonusMalusInput = str(input("Drabbas bilen av bonus/malus-skatt (j/n)? "))

# Check if bonus/malus
if (bonusMalusInput == 'j' or bonusMalusInput == 'J'):
    isBonusMalus = True

dieselInput = str(input("Är bilen dieseldriven (j/n)? "))
# Check if diesel driven and if so, skip question if eco fuel driven
if (dieselInput == 'j' or dieselInput == 'J'):
    isDiesel = True
else:
    ecoFuelInput = str(input("Är bilen driven av E85 eller gas (j/n)? "))
    # Check if eco fuel driven
    if (ecoFuelInput == 'j' or ecoFuelInput == 'J'):
        isEcoFuel = True


# Output result
if (isBonusMalus == True):
    print("Årsskatt första tre åren: " + str(VehicleTaxCalculation(carbonInput,modelYearInput,isBonusMalus,isDiesel,isEcoFuel)) + " kr")
    print("Efterföljande år: " + str(VehicleTaxCalculation(carbonInput,modelYearInput,False,isDiesel,isEcoFuel)) + " kr")

else:
    print("Årsskatt: " + str(VehicleTaxCalculation(carbonInput,modelYearInput,isBonusMalus,isDiesel,isEcoFuel)) + " kr")