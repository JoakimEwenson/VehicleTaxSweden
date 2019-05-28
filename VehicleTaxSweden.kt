package se.ewenson.vehicletax

/*
 * A small script to calculate the new tax for road vehicles in Sweden.
 * Data is collected from the following address and is subject to change.
 * https://transportstyrelsen.se/sv/vagtrafik/Fordon/Fordonsskatt/Hur-bestams-skattens-storlek/
 *
 * Author: Joakim Ewenson
 * Date: 2019-05-27
 */

// Set up default values for the calculation
var isBonusMalus: Boolean = true
var isEcoFuel: Boolean = false
var isDiesel: Boolean = false
var vehicleTax: Int = 0

// Set up default components
val basicFee: Int = 360
val carbonComponent: Int = 22
val carbonComponentEco: Int = 11
val carbonComponentLevel: Int = 111

// Set up bonus/malus components
val malusComponentLow: Int = 82
val malusComponentHigh: Int = 107
val malusComponentLevelLow: Int = 95
val malusComponentLevelHigh: Int = 140
val malusModelYear: Int = 2018

// Set up diesel components
val dieselComponentLow: Int = 250
val dieselComponentHigh: Int = 500
val dieselComponentModelYear: Int = 2008
val dieselFuelFactor: Double = 13.52
val dieselMultFactor: Double = 2.37

// Set up the calculations
fun VehicleTaxCalculation(carbonOutput: Int, modelYear: Int, isBonusMalus: Boolean, isDiesel: Boolean, isEcoFuel: Boolean): Int
{
    // Initialize by setting the vehicle tax to basic fee
    vehicleTax = basicFee

    // Check if vehicle is under the new bonus/malus system
    if (modelYear >= malusModelYear)
    {
        // Check if bonus/malus-flag is set to true
        if (isBonusMalus)
        {
            // Get the difference between vehicle carbon output and allowed threshold
            if (carbonOutput > malusComponentHigh)
            {
                var malusDiffHigh: Int
                var malusDiffLow: Int
                var malusTaxHigh: Int
                var malusTaxLow: Int

                // Get the difference between output and highest level
                malusDiffHigh = carbonOutput - malusComponentHigh
                malusDiffLow = (carbonOutput - malusDiffHigh) - malusComponentLevelLow

                // Calculate the fee
                malusTaxHigh = malusDiffHigh * malusComponentHigh
                malusTaxLow = malusDiffLow * malusComponentLow

                vehicleTax += malusTaxHigh
                vehicleTax += malusTaxLow
            }
            else
            {
                vehicleTax += (carbonOutput - malusComponentLevelLow) * malusComponentLow
            }
        }
        // Check if eco fuel is used and if so, calculate with lower fee
        if (isEcoFuel)
        {
            vehicleTax += (carbonOutput - carbonComponentLevel) * carbonComponentEco
        }
        // Check if diesel is used and if so, add additional taxes
        if (isDiesel)
        {
            // Initialize variables
            var dieselComponentFee: Int

            if (!isBonusMalus)
            {
                // Check carbon output above threshold
                if (carbonOutput > carbonComponentLevel)
                {
                    vehicleTax += (carbonOutput - carbonComponentLevel) * carbonComponent
                }
            }
            // Check if vehicle is newer than current threshold
            if (modelYear >= dieselComponentModelYear)
            {
                dieselComponentFee = dieselComponentLow
            }
            else {
                dieselComponentFee = dieselComponentHigh
            }

            vehicleTax += ((carbonOutput * dieselFuelFactor) + dieselComponentFee).toInt()
        }
    }

    // If vehicle is not under the new system, calculate the tax according to older model
    else
    {
        // Get the difference between vehicle carbon output and allowed threshold
        var carbonDiff: Int = carbonOutput - carbonComponentLevel

        if (carbonDiff > 0)
        {
            // Check if vehicle use eco fuel and if so, use a lower fee
            if (isEcoFuel)
            {
                vehicleTax += carbonDiff * carbonComponentEco
            }
            else
            {
                vehicleTax += carbonDiff * carbonComponent
            }
        }
        if (isDiesel)
        {
            var dieselComponentFee: Int

            if (modelYear >= dieselComponentModelYear)
            {
                dieselComponentFee = dieselComponentLow
            }
            else
            {
                dieselComponentFee = dieselComponentHigh
            }

            vehicleTax = ((vehicleTax * dieselMultFactor) + dieselComponentFee).toInt()
        }
    }

    // Check if calculated tax is below the minimum fee
    if (vehicleTax < basicFee)
    {
        vehicleTax = basicFee
    }

    return vehicleTax
}