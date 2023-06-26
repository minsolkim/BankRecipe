package com.example.bankrecipe.ui.sign

import android.app.Person
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.bankrecipe.MainActivity
import com.example.bankrecipe.Utils.FBAuth
import com.example.bankrecipe.databinding.ActivitySignUpUserBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class SignUpUser : AppCompatActivity() {
    private var auth : FirebaseAuth? = null
    private var fbFirestore : FirebaseFirestore? = null
    private lateinit var binding: ActivitySignUpUserBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        fbFirestore = FirebaseFirestore.getInstance()
        binding = ActivitySignUpUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSignUpConfirm.setOnClickListener {
            val userName = binding.signUpUsername.text.toString()
            val userID = binding.signUpUserID.text.toString()
            val userPassword = binding.signUpUserPassword.text.toString()
            val userPassword2 = binding.signUpUserPassword2.text.toString()
            val userEmail = binding.signUpEmail.text.toString()
            val userType = "user"
            if(userName.isEmpty()) //제약조건
                Toast.makeText(this,"이름을 입력하세요.", Toast.LENGTH_SHORT).show()
            else if(userID.isEmpty()) //현재 이메일과 아이디도 동일하게 두는 중.
                Toast.makeText(this,"아이디를 입력하세요.", Toast.LENGTH_SHORT).show()
            else if(userPassword.isEmpty())
                Toast.makeText(this,"비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show()
            else if(userPassword2.isEmpty())
                Toast.makeText(this,"비밀번호 재확인이 필요합니다.", Toast.LENGTH_SHORT).show()
            else if(userEmail.isEmpty())
                Toast.makeText(this,"이메일이 필요합니다.", Toast.LENGTH_SHORT).show()
            else if(userPassword != userPassword2)
                Toast.makeText(this, "비밀번호를 다시 확인해주세요.", Toast.LENGTH_SHORT).show()
            else
                createAccount(userName,userPassword,userEmail, userID, userType)
            //다이얼로그 띄우기 : 계정 생성 후 바로 로그인할지 아닐지 결정하는 부분을 띄울지 선택하기
        }
        binding.btnSignUpUserBack.setOnClickListener {
            onBackPressed()
        }
    }
    private fun enrollUserInformation(Name:String, Password: String, Email: String, ID : String, userType : String){ //유저정보 업데이트
        val profileUpdates = userProfileChangeRequest { 
            displayName = Name
        }
        val user = FBAuth.auth.currentUser
        user!!.updateProfile(profileUpdates).addOnCompleteListener { task ->
            if(task.isSuccessful){
                //회원가입성공했을때 유저 정보 넣는거니까 성공했을땐 아무것도 할 게 없다.
                //여기서 파이어 스토어에 유저 정보 등록 해야할 듯
                var userInfo = PersonalData()
                userInfo.ID = ID
                userInfo.UserName = Name
                userInfo.Password = Password
                userInfo.userType = userType
                userInfo.userUid = auth?.uid
                userInfo.Email = Email
                fbFirestore?.collection(userType)?.document(auth?.uid.toString())?.set(userInfo)
                //이렇게 하면 사용자 유형에 따라 컬렉션이 나뉘어지지 않을까?
            }
        }
    }
    private fun createAccount(Name:String,password:String, Email:String, userID : String, userType: String){
        //회원가입 성공을 알림.
        //로그인하고 메인액티비티 이동.
        auth?.createUserWithEmailAndPassword(Email,password)
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    enrollUserInformation(Name, password, Email, userID, userType) //유저정보 업데이트 하고
                    MoveMainActivity(auth?.currentUser)
                    Toast.makeText(this, "회원가입 성공"+"\n"+"${Name}"+"님 환영합니다.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "회원가입 실패", Toast.LENGTH_SHORT).show()
                }
            }
    }
    private fun MoveMainActivity(user: FirebaseUser?){
        if(user!=null){
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("userUid","${user.uid}")
            finishAffinity() //새로운 스택을 생성하지 않고 기존 스택 제거
            startActivity(intent)
        }
    }
}