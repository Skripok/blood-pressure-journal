
document.addEventListener('DOMContentLoaded', () => {
    const form = document.querySelector('[data-editable-form]');
    const systolic = document.getElementById('systolic');
    const diastolic = document.getElementById('diastolic');

    if (!form || !systolic || !diastolic) {
        return;
    }

    // Validate the relationship between blood pressure values
    function validateBloodPressure() {
        systolic.setCustomValidity('');

        if (systolic.value === '' || diastolic.value === '') {
            return;
        }

        // Let native number validation handle invalid values
        if (systolic.validity.badInput ||
            diastolic.validity.badInput) {
            return;
        }

        const systolicValue = Number(systolic.value);
        const diastolicValue = Number(diastolic.value);

        if (systolicValue <= diastolicValue) {
            systolic.setCustomValidity(
                'Систолічний тиск повинен бути більшим за діастолічний'
            );
        }
    }

    // Revalidate whenever either value changes
    systolic.addEventListener('input', validateBloodPressure);
    diastolic.addEventListener('input', validateBloodPressure);

    // Validate existing values when the form loads
    validateBloodPressure();
});
