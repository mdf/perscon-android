
package horizon.perscon;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class ConfigActivity extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        TextView tv = new TextView(this);
        tv.setText("config activity");
        setContentView(tv);
    }
}
