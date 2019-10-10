/**
 * A small script to calculate the new tax for road vehicles in Sweden
 * Data is collected from the following address and is subject to change
 * https://transportstyrelsen.se/sv/vagtrafik/Fordon/Fordonsskatt/Hur-bestams-skattens-storlek/
 * 
 * Author: Joakim Ewenson
 * Date: 2019-10-10
 */

// Set up default value for later use
var vehicleTaxResult = 0

// Set up default components
let basicFee = 360
let carbonComponent = 22
let carbonComponentEco = 11
let carbonComponentThreshold = 111

// Set up bonus/malus components
let malusComponentFeeLow = 82
let malusComponentFeeHigh = 107
let malusComponentLevelLow = 95
let malusComponentLevelHigh = 140
let malusModelYear = 2018

// Set up specific diesel components
let dieselComponentFeeLow = 250
let dieselComponentFeeHigh = 500
let dieselComponentModelYear = 2008
let dieselFuelFactor = 13.52
let dieselMultFactor = 2.37

func VehicleTaxCalculation(
    carbonOutput: Int,
    modelYear: Int,
    isBonusMalus: Bool,
    isDiesel: Bool,
    isEcoFuel: Bool
) -> Int {
    // Initialize by setting vehicle tax to basic, minimum fee
    vehicleTaxResult += basicFee
    
    // Check if vehicle is under the new bonus/malus system
    if (modelYear >= malusModelYear) {
        if (isBonusMalus) {
            // Get the difference between vehicle carbon output and maximum allowed output
            if (carbonOutput > malusComponentLevelHigh) {
                // Initialize variables
                var malusDiffHigh, malusDiffLow, malusTaxHigh, malusTaxLow: Int
                
                // Get the difference between output and highest level
                malusDiffHigh = carbonOutput - malusComponentLevelHigh
                malusDiffLow = (carbonOutput - malusDiffHigh) - malusComponentLevelLow
                
                // Calculate the fee
                malusTaxHigh = malusDiffHigh * malusComponentFeeHigh
                malusTaxLow = malusDiffLow * malusComponentFeeLow
                
                vehicleTaxResult += malusTaxHigh
                vehicleTaxResult += malusTaxLow
            }
            else {
                vehicleTaxResult += (carbonOutput - malusComponentLevelLow) * malusComponentFeeLow
            }
        }
        // Check if vehicle is eco fuel driven
        if (isEcoFuel) {
            vehicleTaxResult += (carbonOutput - carbonComponentThreshold) * carbonComponentEco
        }
        // Check if vehicle is diesel fuel driven
        if (isDiesel) {
            // Initialize variables
            var dieselComponentFee: Int
            
            if (!isBonusMalus) {
                // Check carbon output above threshold
                if (carbonOutput > carbonComponentThreshold) {
                    vehicleTaxResult += (carbonOutput - carbonComponentThreshold) * carbonComponent
                }
            }
            // Check if vehicle is newer than current level
            if (modelYear >= dieselComponentModelYear) {
                dieselComponentFee = dieselComponentFeeLow
            }
            else {
                dieselComponentFee = dieselComponentFeeHigh
            }
            
            vehicleTaxResult += Int((Double(carbonOutput) * dieselFuelFactor) + Double(dieselComponentFee))
        }
    }
    // If vehicle is older than the new system, calculate according to older model
    else {
        // Get the difference between vehicle carbon output and allowed threshold
        let carbonDiff = carbonOutput - carbonComponentThreshold
        if (carbonDiff > 0) {
            // Check if vehicle use eco fuel and if so, use a lower fee
            if (isEcoFuel) {
                vehicleTaxResult += carbonDiff * carbonComponentEco
            }
            else {
                vehicleTaxResult += carbonDiff * carbonComponent
            }
        }
        if (isDiesel) {
            var dieselComponentFee: Int
            if (modelYear >= dieselComponentModelYear) {
                dieselComponentFee = dieselComponentFeeLow
            }
            else {
                dieselComponentFee = dieselComponentFeeHigh
            }
            vehicleTaxResult = Int((Double(vehicleTaxResult) * dieselMultFactor) + Double(dieselComponentFee))
        }
    }
    
    // Check if calculated tax is below the minimum, basic fee
    if (vehicleTaxResult < basicFee) {
        vehicleTaxResult = basicFee
    }
    
    // Output vehicle tax from the calculation as an Integer number
    return vehicleTaxResult
}