/*
 * A small script to calculate the new tax for road vehicles in Sweden
 * Data is collected from the following address and is subject to change
 * https://transportstyrelsen.se/sv/vagtrafik/Fordon/Fordonsskatt/Hur-bestams-skattens-storlek/
 * 
 * Author: Joakim Ewenson
 * Date: 2019-04-11
 */

// Set up default values for later use
var vehicleTax = 0;

// Set up default components
const basicFee = 360;                        // In swedish krona
const carbonComponent = 22;                  // In swedish krona
const carbonComponentEco = 11;               // In swedish krona
const carbonComponentThreshold = 111;        // g / km

// Set up bonus/malus components
const malusComponentFeeLow = 82;             // In swedish krona
const malusComponentFeeHigh = 107;           // In swedish krona
const malusComponentLevelLow = 95;           // g / km
const malusComponentLevelHigh = 140;         // g / km
const malusModelYear = 2018;                 // Year (yyyy)

// Set up specific diesel components
const dieselComponentFeeLow = 250;          // In swedish krona
const dieselComponentFeeHigh = 500;         // In swedish krona
const dieselComponentYear = 2008;           // Year (yyyy)
const dieselFuelFactor = 13.52;             // If newer than 1 july 2018, multiply co2 emission by this
const dieselMultFactor = 2.37;              // If older than 1 july 2018, multiply total tax by this

// Main calculation
function VehicleTaxCalculation(carbonOutput, modelYear, isBonusMalus, isDieselFuel, isEcoFuel) {
    // Initialize by setting the vehicle tax to basic fee
    vehicleTax = basicFee;

    // Check if vehicle is under the new bonus/malus model
    if (modelYear >= malusModelYear) {
        // Check if Bonus/Malus-flag is set to true
        if (isBonusMalus) {
            // Get the difference between vehicle carbon output and allowed threshold
            if (carbonOutput > malusComponentLevelHigh) {
                // Initialize variables
                var malusDifferenceHigh, malusDifferenceLow, malusTaxHigh, malusTaxLow, carbonDifference, carbonFee;

                // Get the difference between output and high level
                malusDifferenceHigh = carbonOutput - malusComponentLevelHigh;
                malusDifferenceLow = (carbonOutput - malusDifferenceHigh) - malusComponentLevelLow;

                // Calculate the fee
                malusTaxHigh = malusDifferenceHigh * malusComponentFeeHigh;
                malusTaxLow = malusDifferenceLow * malusComponentFeeLow;

                vehicleTax += malusTaxHigh;
                vehicleTax += malusTaxLow;
            }
            else {
                carbonDifference = carbonOutput - malusComponentLevelLow;
                carbonFee = carbonDifference * malusComponentFeeLow;

                vehicleTax += carbonFee;
            }
        }
        // Check if Bonus/Malus-flag is set to false
        if (!isBonusMalus) {
            vehicleTax += (carbonOutput - carbonComponentThreshold) * carbonComponent;
        }
        // Check if eco fuel is used and if so, calculate lower taxes
        if (isEcoFuel) {
            vehicleTax += (carbonOutput - carbonComponentThreshold) * carbonComponentEco;
        }
        // Check if diesel is used and if so, add additional taxes
        if (isDieselFuel) {
            // Initialize variables
            var dieselComponentFee;
            if (isBonusMalus == false) {
                // Check carbon output above threshold
                if (carbonOutput > carbonComponentThreshold) {
                    vehicleTax += (carbonOutput - carbonComponentThreshold) * carbonComponent;
                }
            }
            // Check if vehicle is newer than current level
            if (modelYear >= dieselComponentYear) {
                dieselComponentFee = dieselComponentFeeLow
            }
            else {
                dieselComponentFee = dieselComponentFeeHigh
            }
            vehicleTax += (carbonOutput * dieselFuelFactor) + dieselComponentFee;
        }
    }

    // If not under the new system, calculate the tax according to old model
    else {
        // Get the difference between vehicle carbon output and allowed threshold
        var carbonDifference = carbonOutput - carbonComponentThreshold;
        if (carbonDifference > 0) {
            // Check if vehicle use eco fuel and if so, use a lower fee for the calculation
            if (isEcoFuel) {
                carbonFee = carbonDifference * carbonComponentEco;
            }
            else {
                carbonFee = carbonDifference * carbonComponent;
            }
            // Add carbon fee to vehicle tax
            vehicleTax += carbonFee;
        }
        // Check if diesel is used and if so, add additional taxes
        if (isDieselFuel) {
            var dieselComponentFee;
            if (modelYear >= dieselComponentYear) {
                dieselComponentFee = dieselComponentFeeLow
            }
            else {
                dieselComponentFee = dieselComponentFeeHigh
            }
            vehicleTax = (vehicleTax * dieselMultFactor) + dieselComponentFee
        }
    }

    // Check if calculated tax is below the minimum, basic fee
    if (vehicleTax < basicFee) {
        vehicleTax = basicFee;
    }

    // Output vehicle tax from function
    return vehicleTax;
}