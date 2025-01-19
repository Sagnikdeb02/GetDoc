## Task distribution
1. Prapti:
   1. Define 3 ViewModel. Auth, Doctor and Patient.
   2. Define 3 UiState for them.
2. Nasim:
   1. Make all UI components and Screens of Patient feature dynamic.


## How to define a composable component
1. keep a modifier as a default parameter with a default argument.
2. take all the UI state from outside the composable function.
3. Hoist all the UI event to the outside of the function.
4. Give the component a meaningful name with the word Component at the end.
5. Document your code for better understanding.
```agsl
/**
 * This component will be used in [PatientAppointmentScreen] to show doctors details with
 * [Button] Reschedule and [Button] Cancel
 */
//@Composable
fun DoctorCardComponent(
    doctorName: String,
    modifier: Modifier = Modifier,
    onRescheduleClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    Column {
        Text("name: $doctorName")
        Button(
            onClick = onRescheduleClick,
        ) {
            Text("Reschedule")
        }
    }
}
```

## How to define a screen content composable
```agsl
/**
 * This content will be displayed in [PatientAppointmentScreen]
 */
//@Composable
fun PatientAppointmentScreenContent(
    activeAppointmentList: List<String>,
    previousAppointmentList: List<String>,
    modifier: Modifier = Modifier,
    onRescheduleClick: (String) -> Unit,
    onCancelClick: (String) -> Unit
) {
    // define the whole screen here.
}
```

## How to define a ViewModel
1. Define UiState for all the major screens.
2. Define a viewmodel for every feature.
3. Create UiState object inside viewmodel.
4. call firebase functions inside viewmodel.