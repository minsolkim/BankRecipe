package com.solid.bankrecipe.ui.myPage

import android.content.Intent
import android.opengl.Visibility
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import com.solid.bankrecipe.MainActivity
import com.solid.bankrecipe.Utils.FBAuth
import com.solid.bankrecipe.Utils.FBRef
import com.solid.bankrecipe.databinding.FragmentMyPageBinding
import com.solid.bankrecipe.ui.chat.ChatViewModel
import com.solid.bankrecipe.ui.community.MyCommunity
import com.solid.bankrecipe.ui.map.MapActivity
import com.solid.bankrecipe.ui.sign.PersonalData
import com.solid.bankrecipe.ui.sign.SignIn
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore

private var userType : String? = null
private var FBFireStore : FirebaseFirestore? = null
class MyPageFragment : Fragment() {

    private var _binding: FragmentMyPageBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val myPageViewModel =
            ViewModelProvider(this).get(MyPageViewModel::class.java)

        _binding = FragmentMyPageBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //https://firebase.google.com/docs/auth/android/manage-users?hl=ko 참고
        //val str = FBAuth.auth.currentUser?.uid //toString일 때 문제
        FBFireStore = FirebaseFirestore.getInstance()
        var userInfoRef = FBFireStore!!.collection("user")
        binding.btneditprofileCheck.visibility = View.GONE
        if (FBAuth.getDisplayName() == "null") { //사용자가 없을 때
            //toString의 경우 null을 "null"로 리턴
            //로그인 부분 등장
            mypageInit(binding)
        } else{ //로그인 했을 때
            userInfoRef.get().addOnCompleteListener { task ->
                if(task.isSuccessful){
                    for(i in task.result!!){
                        if(i.id == FBAuth.getUid()){
                            userType = i.data["userType"].toString()
                            LoadMypageData(userType!!,binding)
                        }
                    }
                }
            }
            binding.btnLogout.visibility = View.VISIBLE
            binding.mypageWelcomeText.text = FBAuth.getDisplayName()+" 님 \n 환영합니다."
            binding.btnEditProfile.visibility = View.VISIBLE
            //openingTxt.setText(userType)

            //판매내역 버튼 눌렀을 때
            binding.btnSell.setOnClickListener {
                val intent = Intent(activity, MyCommunity::class.java)
                startActivity(intent)
            }

            //binding.imageView2(기본 이미지)
            /*user!!.updateProfile(profileUpdates).addOnCompleteListener { task -> if(task.isSuccessful){
                Toast.makeText(this.context, "${FBAuth.auth.currentUser?.displayName.toString()}"+"님 환영합니다.ㅅ", Toast.LENGTH_SHORT).show()
            }
            }*/
            // seller 마이페이지x , 기본 유저 페이지로 보여주기
        }
        binding.btnLogout.setOnClickListener {
            if(FBAuth.getDisplayName() == "null"){ //로그인 버튼
                val intent = Intent(activity, SignIn::class.java)
                startActivity(intent)
                //manifest에서 .은 현재 패키지를 의미함.
            }
            else{
                mypageInit(binding)
                FBAuth.auth.signOut()
            }
        }

        binding.btnEditProfile.setOnClickListener { 
            //프로필 수정
            if(userType.equals("seller")){
                isabledSellerEditTxt(binding)
            }
            binding.btneditprofileCheck.visibility = View.VISIBLE
            binding.btnEditProfile.visibility = View.GONE
            binding.btnLogout.visibility = View.GONE
            //editText에 대해선 setText로 우선 수정되니까 다시 데이터를 가져올 필요는 없을 듯함.
        }

        binding.btneditprofileCheck.setOnClickListener {
            disabledSellerEditTxt(binding)
            //파이어베이스에 내용을 수정해야함.
            binding.btneditprofileCheck.visibility = View.GONE
            binding.btnEditProfile.visibility = View.VISIBLE
            binding.btnLogout.visibility = View.VISIBLE

        }

        return root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
private fun LoadMypageData(userType : String, binding : FragmentMyPageBinding) {
    if(userType.equals("seller")){ //판매자
        var userMypageRef = FBFireStore?.collection("MypageData")
        userMypageRef?.get()?.addOnCompleteListener { task ->
            if(task.isSuccessful){
                for(i in task.result!!){
                        if(i.id == FBAuth.getUid()){
                            binding.holidayTxt.setText(i.data["holiday"].toString())
                            binding.homepageTxt.setText(i.data["homepage"].toString())
                            binding.LocationTxt.setText(i.data["location"].toString())
                            binding.openingTxt.setText(i.data["openTime"].toString())
                            binding.telNumberTxt.setText(i.data["tel"].toString())
                        }
                }
            }
        }
        binding.sellerPageInfo.visibility = View.VISIBLE
        disabledSellerEditTxt(binding)
    }
    else{ //일반 사용자
        binding.sellerPageInfo.visibility = View.GONE
    }
}
private fun mypageInit(binding : FragmentMyPageBinding){
    binding.btnLogout.visibility = View.VISIBLE
    binding.btnLogout.text = "로그인"
    binding.mypageWelcomeText.text = "로그인이 필요합니다."
    binding.btnEditProfile.visibility = View.GONE
    binding.sellerPageInfo.visibility = View.GONE
    binding.btneditprofileCheck.visibility = View.GONE
}
fun disabledSellerEditTxt(binding : FragmentMyPageBinding){
    binding.holidayTxt.isEnabled = false
    binding.homepageTxt.isEnabled = false
    binding.LocationTxt.isEnabled = false
    binding.openingTxt.isEnabled = false
    binding.telNumberTxt.isEnabled = false
}
fun isabledSellerEditTxt(binding : FragmentMyPageBinding){
    binding.holidayTxt.isEnabled = true
    binding.homepageTxt.isEnabled = true
    binding.LocationTxt.isEnabled = true
    binding.openingTxt.isEnabled = true
    binding.telNumberTxt.isEnabled = true
}