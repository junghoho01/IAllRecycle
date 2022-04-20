package com.example.iallrecycle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.iallrecycle.adapters.NewsAdapter

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.example.iallrecycle.databinding.ActivityMapsBinding
import com.example.iallrecycle.dataclass.News
import com.example.iallrecycle.dataclass.Recycler
import com.google.android.gms.maps.model.*
import com.google.firebase.database.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var dbref: DatabaseReference

    // Declare an array list for markers
    private var locationArrayList: ArrayList<LatLng>? = null
    private var recycles = arrayListOf<Recycler>()
    private var nameArrayList: ArrayList<String>? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nameArrayList = ArrayList()
        locationArrayList = ArrayList()
        getCoordinate()

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        for (i in locationArrayList!!.indices) {
//            Toast.makeText(applicationContext, "Ada data" + "$locationArrayList", Toast.LENGTH_SHORT).show()

//            val markerOptions = MarkerOptions()
//            markerOptions.position(locationArrayList!![i])
//            val marker: Marker?  = googleMap?.addMarker(markerOptions)
//            val bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.ic_recycle)
//            marker?.setIcon(bitmapDescriptor)

            map.addMarker(MarkerOptions().position(locationArrayList!![i]).title(nameArrayList!![i]))
            map.animateCamera(CameraUpdateFactory.zoomTo(25f))
            map.moveCamera(CameraUpdateFactory.newLatLng(locationArrayList!!.get(i)))
        }

    }

    // Not working from here *************
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.map_options, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        // Change the map type based on the user's selection.
        R.id.normal_map -> {
            map.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            map.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            map.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            map.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
    // Until here ******************

    private fun getCoordinate(){

        dbref = FirebaseDatabase.getInstance().getReference("Users")
        dbref.orderByChild("userType").equalTo("recyler").addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(userSnapshot in snapshot.children){
                        val userRecycler = userSnapshot.getValue(Recycler::class.java)
                        recycles.add(userRecycler!!)
                        val tempLat = userRecycler.latitude.toString().toDouble()
                        val tempLong = userRecycler.longitude.toString().toDouble()
                        val tempCoordinate = LatLng(tempLat, tempLong)
                        locationArrayList?.add(tempCoordinate)
                        nameArrayList?.add(userRecycler.companyName.toString())
//                        Toast.makeText(applicationContext, "${tempCoordinate}", Toast.LENGTH_SHORT).show()
                    }
                }
                //checkDataExist()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "Data fail", Toast.LENGTH_SHORT).show()
            }

        })

    }

    private fun checkDataExist(){
        if(locationArrayList.isNullOrEmpty()){
            Toast.makeText(applicationContext, "$locationArrayList", Toast.LENGTH_SHORT).show()
            Toast.makeText(applicationContext, "It is empty", Toast.LENGTH_SHORT).show()
        }
        else{
            Toast.makeText(applicationContext, "Has data", Toast.LENGTH_SHORT).show()
        }
    }
}