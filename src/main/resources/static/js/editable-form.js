
document.addEventListener('DOMContentLoaded', () => {

    // Initialize all editable forms
    document.querySelectorAll('[data-editable-form]')
        .forEach(initializeEditableForm);

    function initializeEditableForm(form) {

        const primaryAction = form.querySelector(
            '[data-editable-primary-action]'
        );

        const cancelAction = form.querySelector(
            '[data-editable-cancel-action]'
        );

        const formMode = form.dataset.formMode;
        const fields = form.querySelectorAll(
            'input:not([type="hidden"]), select, textarea'
        );

        if (!primaryAction) {
            return;
        }

        // CREATE mode starts with enabled fields
        let isEditMode = formMode === 'CREATE';

        // Remember fields that must always stay disabled
        const permanentlyDisabledFields = new Set(
            form.querySelectorAll('[data-always-disabled]')
        );

        // Capture all field values, including disabled fields
        function getFormState() {
            return Array.from(fields).map(field => ({
                name: field.name,
                value: field.type === 'checkbox'
                    ? field.checked
                    : field.value
            }));
        }

        const initialState = JSON.stringify(getFormState());

        function isFormDirty() {
            return JSON.stringify(getFormState()) !== initialState;
        }

        let isSubmitting = false;

        // Allow navigation after a valid form submission
        form.addEventListener('submit', event => {

            if (!form.checkValidity()) {
                return;
            }

            isSubmitting = true;
        });

        // Warn about unsaved changes before leaving the page
        window.addEventListener('beforeunload', event => {

            if (isSubmitting || !isEditMode || !isFormDirty()) {
                return;
            }

            event.preventDefault();
            event.returnValue = '';
        });

        // Enable or disable form editing
        function setEditMode(enabled) {
            isEditMode = enabled;

            fields.forEach(field => {
                field.disabled = !enabled ||
                    permanentlyDisabledFields.has(field);
            });

            primaryAction.textContent = !enabled
                ? 'Редагувати'
                : formMode === 'CREATE'
                    ? 'Створити'
                    : 'Зберегти';
        }

        // Validate required text fields that must not contain only whitespace
        const trimRequiredFields = form.querySelectorAll('[data-trim-required]');

        trimRequiredFields.forEach(field => {

            function validateField() {
                const isWhitespaceOnly =
                    field.value.length > 0 && field.value.trim().length === 0;

                field.setCustomValidity(
                    isWhitespaceOnly
                        ? 'Поле не може містити лише пробіли'
                        : ''
                );
            }

            field.addEventListener('input', validateField);
            field.addEventListener('blur', validateField);

            // Validate values already present when the form loads
            validateField();
        });

        // Handle Edit / Save / Create
        primaryAction.addEventListener('click', () => {

            if (!isEditMode) {
                setEditMode(true);
                return;
            }

            // Submit form to the server
            form.requestSubmit();
        });


        // Track form changes
        fields.forEach(field => {
            field.addEventListener('input', () => {
                console.log('Form dirty:', isFormDirty());
            });
        });

        // Initialize form state
        setEditMode(isEditMode);
    }
});
