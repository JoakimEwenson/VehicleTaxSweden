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

}