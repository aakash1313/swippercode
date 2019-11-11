package com.example.swipper.dashboard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.WorkerThread
import androidx.appcompat.app.AppCompatActivity
import com.example.swipper.R
import com.example.swipper.SwipperBaseActivity
import com.example.swipper.login.LoginActivity
import com.lorentzos.flingswipe.SwipeFlingAdapterView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : SwipperBaseActivity() {

    private var al = ArrayList<String>()
    private var arrayAdapter: ArrayAdapter<String>? = null
    private var i = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        addValsToList()

        //choose your favorite adapter
        arrayAdapter = ArrayAdapter(this,
            R.layout.swipe_list_item,
            R.id.helloText, al)

        //set the listener and the adapter
        frame.adapter = arrayAdapter
        frame.setFlingListener(object : SwipeFlingAdapterView.onFlingListener {
            override fun removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!")
                al.removeAt(0)
                arrayAdapter?.notifyDataSetChanged()
            }


            override fun onLeftCardExit(p0: Any?) {
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject
                //Toast.makeText(this@MainActivity, "Busted!", Toast.LENGTH_SHORT).show()
                Log.d("SwipperApp", "User Swipped left")
                CoroutineScope(IO).launch {
                    fakeApiRequest()
                }
                // make an api call on background
            }

            override fun onRightCardExit(p0: Any?) {
               // Toast.makeText(this@MainActivity, "Love it!", Toast.LENGTH_SHORT).show()
                Log.d("SwipperApp", "User Swipped right" )
            }

            override fun onAdapterAboutToEmpty(p0: Int) {
                // Ask for more data here
                addValsToList()
                arrayAdapter?.notifyDataSetChanged()
                Log.d("LIST", "notified")
            }


            override fun onScroll(p0: Float) {
            }
        });

    }

    private suspend fun fakeApiRequest(){
        for (i in 0 until  5)
         getResult1FromApi()
       // getResult1FromApi2()
    }

    private fun addValsToList(){
        al.add("Outlook is the best email client")
        al.add("Dark mode in Gmail is amazing!")
        al.add("Starbucks has the best coffee!")
        al.add("Dagger is going to be stabbed by Co-routines :D")
        al.add("Leg days are the best days!")
        al.add("Tesla is better than Bentley!")
        al.add("We should have a tax-free world!")
    }

    private suspend fun getResult1FromApi() : String{
        logThread("getResult1FromApi()")
        //delay a single co-routine
        delay(1000)
       return "Result #1"
    }


    @WorkerThread
    private suspend fun getResult1FromApi2() : String{
        logThread("getResult1FromApi()")
        //delay a single co-routine
        delay(1000)
        return "Result #1"
    }

    private fun logThread(methodName: String){
           println("debug:${methodName} : ${Thread.currentThread().name}")

    }

    companion object {
        fun newIntent(context: Context?) = Intent(context, MainActivity:: class.java)
    }

}
