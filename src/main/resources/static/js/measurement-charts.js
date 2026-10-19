
'use strict';

// ============================================================
// Chart period settings
// ============================================================

const periodPreset = document.getElementById('periodPreset');
const customPeriodBlock = document.getElementById('customPeriodBlock');
const customDays = document.getElementById('customDays');
const daysInput = document.getElementById('days');
const chartFilterForm = document.getElementById('chartFilterForm');

const presetValues = ['7', '30', '90', '365'];

function initializePeriodSettings() {
    if (!periodPreset || !customPeriodBlock ||
        !customDays || !daysInput || !chartFilterForm) {
        return;
    }

    // Restore the selected period after page reload.
    if (presetValues.includes(daysInput.value)) {
        periodPreset.value = daysInput.value;
    } else {
        periodPreset.value = 'custom';
        customDays.value = daysInput.value;
    }

    periodPreset.addEventListener('change', updatePeriod);

    chartFilterForm.addEventListener('submit', function (event) {
        if (periodPreset.value === 'custom') {
            const value = Number(customDays.value);

            if (!Number.isInteger(value) || value < 1 || value > 365) {
                event.preventDefault();
                customDays.setCustomValidity(
                    'Введіть ціле число від 1 до 365.'
                );
                customDays.reportValidity();
                return;
            }

            customDays.setCustomValidity('');
            daysInput.value = String(value);
        } else {
            daysInput.value = periodPreset.value;
        }
    });

    customDays.addEventListener('input', function () {
        customDays.setCustomValidity('');
    });

    updatePeriod();
}

function updatePeriod() {
    const isCustom = periodPreset.value === 'custom';

    customPeriodBlock.classList.toggle('d-none', !isCustom);
    customDays.disabled = !isCustom;
    customDays.required = isCustom;

    if (!isCustom) {
        customDays.setCustomValidity('');
        daysInput.value = periodPreset.value;
    }
}


// ============================================================
// Chart helpers
// ============================================================

function formatDateTime(dateTime) {
    const [date, time] = dateTime.split('T');
    const [year, month, day] = date.split('-');

    return `${day}.${month}.${year} ${time.substring(0, 5)}`;
}

function formatShortDate(date) {
    const [year, month, day] = date.split('-');

    return `${day}.${month}.${year.slice(-2)}`;
}


// ============================================================
// Alternating day background plugin
// ============================================================

function createDayBackgroundPlugin(measurements) {
    return {
        id: 'dayBackground',

        beforeDatasetsDraw(chart) {
            const {ctx, chartArea, scales} = chart;
            const xScale = scales.x;

            if (!measurements.length) {
                return;
            }

            ctx.save();

            let startIndex = 0;
            let dayIndex = 0;

            while (startIndex < measurements.length) {
                const currentDate =
                    measurements[startIndex].measuredAt.substring(0, 10);

                let endIndex = startIndex;

                // Find the last measurement of the current day.
                while (
                    endIndex + 1 < measurements.length &&
                    measurements[endIndex + 1].measuredAt.substring(0, 10)
                    === currentDate
                    ) {
                    endIndex++;
                }

                // Calculate boundaries between neighboring day groups.
                const left = startIndex === 0
                    ? chartArea.left
                    : (
                    xScale.getPixelForValue(startIndex - 1) +
                    xScale.getPixelForValue(startIndex)
                ) / 2;

                const right = endIndex === measurements.length - 1
                    ? chartArea.right
                    : (
                    xScale.getPixelForValue(endIndex) +
                    xScale.getPixelForValue(endIndex + 1)
                ) / 2;

                // Alternate white and light-gray backgrounds.
                if (dayIndex % 2 === 1) {
                    ctx.fillStyle = 'rgba(108, 117, 125, 0.10)';
                    ctx.fillRect(
                        left,
                        chartArea.top,
                        right - left,
                        chartArea.bottom - chartArea.top
                    );
                }

                // Draw a subtle vertical boundary between days.
                if (startIndex > 0) {
                    ctx.beginPath();
                    ctx.strokeStyle = 'rgba(108, 117, 125, 0.20)';
                    ctx.lineWidth = 1;
                    ctx.moveTo(left, chartArea.top);
                    ctx.lineTo(left, chartArea.bottom);
                    ctx.stroke();
                }

                startIndex = endIndex + 1;
                dayIndex++;
            }

            ctx.restore();
        }
    };
}


// ============================================================
// Combined blood pressure and pulse chart
// ============================================================

function initializePressureChart(measurements) {
    const canvas = document.getElementById('pressureChart');

    if (!canvas || measurements.length === 0) {
        return;
    }

    const labels = measurements.map(m =>
        formatDateTime(m.measuredAt)
    );

    const dates = measurements.map(m =>
        m.measuredAt.substring(0, 10)
    );

    const systolicValues = measurements.map(m => m.systolic);
    const diastolicValues = measurements.map(m => m.diastolic);
    const pulseValues = measurements.map(m => m.pulse ?? null);

    new Chart(canvas, {
        type: 'line',

        data: {
            labels: labels,
            datasets: [
                {
                    label: 'Систолічний тиск',
                    data: systolicValues,
                    yAxisID: 'yPressure',
                    borderColor: '#dc3545',
                    backgroundColor: '#dc3545',
                    tension: 0.2,
                    pointRadius: 3,
                    pointHoverRadius: 6
                },
                {
                    label: 'Діастолічний тиск',
                    data: diastolicValues,
                    yAxisID: 'yPressure',
                    borderColor: '#0d6efd',
                    backgroundColor: '#0d6efd',
                    tension: 0.2,
                    pointRadius: 3,
                    pointHoverRadius: 6
                },
                {
                    label: 'Пульс',
                    data: pulseValues,
                    yAxisID: 'yPulse',
                    borderColor: '#198754',
                    backgroundColor: '#198754',
                    borderDash: [6, 4],
                    tension: 0.2,
                    pointRadius: 3,
                    pointHoverRadius: 6,
                    spanGaps: false
                }
            ]
        },

        options: {
            responsive: true,
            maintainAspectRatio: false,

            interaction: {
                mode: 'index',
                intersect: false
            },

            plugins: {
                legend: {
                    display: true,
                    onClick: function (event, item, legend) {
                        const chart = legend.chart;
                        const datasetIndex = item.datasetIndex;

                        chart.setDatasetVisibility(
                            datasetIndex,
                            !chart.isDatasetVisible(datasetIndex)
                        );

                        chart.update();
                    }
                },

                tooltip: {
                    enabled: true,
                    callbacks: {
                        title: function (items) {
                            if (!items.length) {
                                return '';
                            }

                            return labels[items[0].dataIndex];
                        },

                        label: function (context) {
                            const value = context.parsed.y;

                            if (value === null) {
                                return '';
                            }

                            const unit = context.dataset.yAxisID === 'yPulse'
                                ? 'уд/хв'
                                : 'мм рт. ст.';

                            return `${context.dataset.label}: ${value} ${unit}`;
                        }
                    }
                }
            },

            scales: {
                yPressure: {
                    type: 'linear',
                    position: 'left',
                    title: {
                        display: true,
                        text: 'Тиск, мм рт. ст.'
                    }
                },

                yPulse: {
                    type: 'linear',
                    position: 'right',
                    title: {
                        display: true,
                        text: 'Пульс, уд/хв'
                    },

                    // Avoid duplicate horizontal grid lines.
                    grid: {
                        drawOnChartArea: false
                    }
                },

                x: {
                    ticks: {
                        autoSkip: false,
                        maxRotation: 0,
                        minRotation: 0,

                        callback: function (value, index) {
                            const currentDate = dates[index];

                            if (!currentDate) {
                                return '';
                            }

                            // Display the date only for the first
                            // measurement of each day.
                            if (
                                index > 0 &&
                                dates[index - 1] === currentDate
                            ) {
                                return '';
                            }

                            return formatShortDate(currentDate);
                        }
                    }
                }
            }
        },

        plugins: [
            createDayBackgroundPlugin(measurements)
        ]
    });
}


// ============================================================
// Initialization
// ============================================================

function initializeCharts() {
    const container = document.getElementById('chartsContainer');

    if (!container) {
        return;
    }

    const measurements = JSON.parse(container.dataset.measurements);

    if (measurements.length === 0) {
        return;
    }

    // Ensure chronological order.
    measurements.sort((a, b) =>
        a.measuredAt.localeCompare(b.measuredAt)
    );

    initializePressureChart(measurements);
}

initializePeriodSettings();
initializeCharts();
