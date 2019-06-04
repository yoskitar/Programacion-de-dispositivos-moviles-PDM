package e.oscarjimfer.pruebatfg;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Calendar calendarInit = Calendar.getInstance();
        int hour = calendarInit.get(Calendar.HOUR_OF_DAY);
        int minute = calendarInit.get(Calendar.MINUTE);
        return new TimePickerDialog(getActivity(),
                (TimePickerDialog.OnTimeSetListener) getActivity(), hour, minute,
                android.text.format.DateFormat.is24HourFormat(getActivity()));
    }
}
