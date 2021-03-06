/**
 * A small script to calculate the new tax for road vehicles in Sweden
 * Data is collected from the following address and is subject to change
 * https://transportstyrelsen.se/sv/vagtrafik/Fordon/Fordonsskatt/Hur-bestams-skattens-storlek/
 * 
 * Author: Joakim Ewenson
 * Date: 2019-05-21
 */

using System;

namespace VehicleTaxSweden
{
    public class VehicleTax
    {
        // Set up default value for later use
        public int vehicleTaxResult = 0;

        // Set up default components
        public const int basicFee = 360;
        public const int carbonComponent = 22;
        public const int carbonComponentEco = 11;
        public const int carbonComponentThreshold = 111;

        // Set up bonus/malus components
        public const int malusComponentFeeLow = 82;
        public const int malusComponentFeeHigh = 107;
        public const int malusComponentLevelLow = 95;
        public const int malusComponentLevelHigh = 140;
        public const int malusModelYear = 2018;

        // Set up specific diesel components
        public const int dieselComponentFeeLow = 250;
        public const int dieselComponentFeeHigh = 500;
        public const int dieselComponentYear = 2008;
        public const double dieselFuelFactor = 13.52;
        public const double dieselMultFactor = 2.37;

        // Main calculation
        public int VehicleTaxCalculation(int carbonOutput, int modelYear, bool isBonusMalus, bool isDiesel, bool isEcoFuel)
        {
            // Initialize by setting vehicle tax to basic, minimum fee
            vehicleTaxResult += basicFee;

            // Check if vehicle is under the new bonus/malus system
            if (modelYear >= malusModelYear)
            {
                if (isBonusMalus)
                {
                    // Get the difference between vehicle carbon output and maximum allowed output
                    if (carbonOutput > malusComponentLevelHigh)
                    {
                        // Initialize variables
                        int malusDiffHigh, malusDiffLow, malusTaxHigh, malusTaxLow;

                        // Get the difference between output and highest level
                        malusDiffHigh = carbonOutput - malusComponentLevelHigh;
                        malusDiffLow = (carbonOutput - malusDiffHigh) - malusComponentLevelLow;

                        // Calculate the fee
                        malusTaxHigh = malusDiffHigh * malusComponentFeeHigh;
                        malusTaxLow = malusDiffLow * malusComponentFeeLow;

                        vehicleTaxResult += malusTaxHigh;
                        vehicleTaxResult += malusTaxLow;
                    }
                    else
                    {
                        vehicleTaxResult += (carbonOutput - malusComponentLevelLow) * malusComponentFeeLow;
                    }
                }
                // Check if eco fuel is used and if so, calculate with lower fee
                if (isEcoFuel)
                {
                    vehicleTaxResult += (carbonOutput - carbonComponentThreshold) * carbonComponentEco;
                }
                // Check if diesel is used and if so, add additional taxes
                if (isDiesel)
                {
                    // Initialize variables
                    int dieselComponentFee;

                    if (!isBonusMalus)
                    {
                        // Check carbon output above threshold
                        if (carbonOutput > carbonComponentThreshold)
                        {
                            vehicleTaxResult += (carbonOutput - carbonComponentThreshold) * carbonComponent;
                        }
                    }
                    // Check if vehicle is newer than current level
                    if (modelYear >= dieselComponentYear)
                    {
                        dieselComponentFee = dieselComponentFeeLow;
                    }
                    else
                    {
                        dieselComponentFee = dieselComponentFeeHigh;
                    }

                    vehicleTaxResult += (int)(carbonOutput * dieselFuelFactor) + dieselComponentFee;
                }
            }

            // If vehicle not under the new system, calculate the tax according to the older model
            else
            {
                // Get the difference between vehicle carbon output and allowed threshold
                int carbonDiff = carbonOutput - carbonComponentThreshold;
                if (carbonDiff > 0)
                {
                    // Check if vehicle use eco fuel and if so, use a lower fee
                    if (isEcoFuel)
                    {
                        vehicleTaxResult += carbonDiff * carbonComponentEco;
                    }
                    else
                    {
                        vehicleTaxResult += carbonDiff * carbonComponent;
                    }
                }
                if (isDiesel)
                {
                    int dieselComponentFee;
                    if (modelYear >= dieselComponentYear)
                    {
                        dieselComponentFee = dieselComponentFeeLow;
                    }
                    else
                    {
                        dieselComponentFee = dieselComponentFeeHigh;
                    }
                    vehicleTaxResult = (int)(vehicleTaxResult * dieselMultFactor) + dieselComponentFee;
                }
            }

            // Check if calculated tax is below the minimum, basic fee
            if (vehicleTaxResult < basicFee)
            {
                vehicleTaxResult = basicFee;
            }

            // Output vehicle tax from the calculation
            return vehicleTaxResult;
        }
    }
}
