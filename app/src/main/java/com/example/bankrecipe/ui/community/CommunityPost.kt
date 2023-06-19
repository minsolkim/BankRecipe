package com.example.bankrecipe.ui.community

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideModule
import com.example.bankrecipe.R
import com.example.bankrecipe.Utils.FBAuth
import com.example.bankrecipe.Utils.FBRef
import com.example.bankrecipe.databinding.ActivityCommunityPostBinding
import com.example.bankrecipe.ui.chat.ChatingActivity
import com.example.bankrecipe.ui.map.MapData
import com.google.firebase.firestore.FirebaseFirestore

@GlideModule
class CommunityPost : AppCompatActivity() {
    private lateinit var binding : ActivityCommunityPostBinding
    lateinit var firestore: FirebaseFirestore
    lateinit var textWriter: TextView
    lateinit var imageIv: ImageView
    lateinit var Title: TextView
    lateinit var Subtext : TextView
    lateinit var make : TextView
    lateinit var period : TextView
    lateinit var time : TextView
    lateinit var writerUid : String
    private lateinit var key: String
    lateinit var map : TextView
    lateinit var price : TextView //물건 가격
    lateinit var chatBtn : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_community_post)
        setContentView(R.layout.activity_community_post)
        firestore = FirebaseFirestore.getInstance()
        key = intent.getStringExtra("key").toString()
        map = findViewById(R.id.post_address)
        val menu = findViewById<ImageView>(R.id.post_menu)
        textWriter = findViewById(R.id.post_name)
        imageIv = findViewById(R.id.ivPostProfile)
        Title = findViewById(R.id.post_title)
        make = findViewById(R.id.post_text2)
        period = findViewById(R.id.post_text3)
        Subtext = findViewById(R.id.post_text4)
        time = findViewById(R.id.post_time)
        price = findViewById(R.id.communityPostPrice)
        chatBtn = findViewById(R.id.post_chat)

        menu.setOnClickListener {
            showDialog()
        }
        if (key!=null){

            val mykey = FBAuth.getUid()
            firestore.collection("photo").document(key).get().addOnCompleteListener {

                task ->
                if(task.isSuccessful){
                    Log.d("key값",key.toString())
                    var photo = task.result?.toObject(CommunityData::class.java)
                    Glide.with(this@CommunityPost).load(photo?.imageUri).into(imageIv)
                        Log.d("이미지 uri null",photo?.imageUri.toString())
                    textWriter.text = photo?.id
                    Title.text = photo?.title
                    make.text = photo?.make
                    period.text = photo?.period
                    Subtext.text = photo?.subtext
                    price.text = photo?.price+"원" //(6/6추가)
                    time.text = FBRef.calculationTime(photo?.date!!.toLong())
                    writerUid = photo?.uid!!
                    if(writerUid.equals(FBAuth.getUid())){
                    firestore.collection("map").document(FBAuth.getUid()).get()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                var mapdata = task.result?.toObject(MapData::class.java)
                                val mapUid = mapdata?.mkey
                                map.text = mapdata?.mapaddress
                                Log.d("나의키", mykey)
                                Log.d("mapkey", mapUid.toString())


                            }
                        }
                        }else {
                        if (writerUid != null) {
                            firestore.collection("map").document(writerUid).get()
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        var mapdata = task.result?.toObject(MapData::class.java)
                                        val mapUid = mapdata?.mkey
                                        map.text = mapdata?.mapaddress


                                    }
                                }
                        }
                    }
                    if(mykey.equals(writerUid)) {
                        Log.d("나의키",mykey)
                        Log.d("작성자키",writerUid.toString())
                        menu.isVisible = true
                        chatBtn.isEnabled = false
                        chatBtn.setBackgroundColor(Color.CYAN)

                    }else {
                        //채팅하기버튼활성화
                        menu.isVisible = false
                        chatBtn.isEnabled = true
                        chatBtn.setBackgroundColor(Color.BLUE)
                        chatBtn.setOnClickListener {
                            val intent = Intent(applicationContext, ChatingActivity::class.java)
                            intent.putExtra("destinationUid", writerUid)
                            startActivity(intent)
                        }
                    }
                }
            }

        }
    }
    private fun showDialog() {
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog,null)
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)
            .setTitle("게시글 수정/삭제")
        val alertDialog = mBuilder.show()

        alertDialog.findViewById<Button>(R.id.deletebtn)?.setOnClickListener {
            firestore.collection("photo").document(key).delete().addOnCompleteListener {
                if(it.isSuccessful)
                    Toast.makeText(this,"삭제완료",Toast.LENGTH_LONG).show()

            }
            alertDialog.dismiss()
            finish()
        }
    }
}