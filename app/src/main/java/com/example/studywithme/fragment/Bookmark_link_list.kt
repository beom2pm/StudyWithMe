package com.example.studywithme.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.*
import android.widget.*
import com.example.studywithme.DialogFragment.DialogAddBookMarkLink
import com.example.studywithme.HtmlParser
import com.example.studywithme.HttpConnection
import com.example.studywithme.R
import com.example.studywithme.bookmark.BookmarkActivity_category
import com.example.studywithme.bookmark.BookmarkActivity_link
import com.example.studywithme.bookmark.BookmarkActivity_link_list_Adapter
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject

/*프래그먼트 생명주기 궁금해서 찾다보니까 주석이 좀 많음..*/
class Bookmark_link_list: Fragment() {
    private var addLinkButton: FloatingActionButton? = null
    private var categoryName: String? = null
    var linkDialogBuilder: AlertDialog.Builder? = null
    var linkDataList_recyclerview: RecyclerView? = null
    var linkDataList_array = arrayListOf<BookmarkActivity_link>()
    var linkDataListAdapter: BookmarkActivity_link_list_Adapter? = null
    var linkData_list_linearLayoutManager: LinearLayoutManager? = null
    var getLinkResult: String? = null
    var linkDialogView: View? = null
    var httpConn: HttpConnection = HttpConnection()
    var sharedLinkURL: String? = null
    var existSharedLinkURL: Boolean = false

    /*프래그먼트가 액티비티에 attach 될 때 호출됨. 인자로 context 가 주어짐. 이건 나중에 쓸 일 있으면 자세히 찾아볼 예정.*/
    override fun onAttach(context: Context?) {
        super.onAttach(context)

        Log.d("링크 프래그먼트 응답 ", "헬로 onAttach")
    }

    /* UI를 제외한 리소스들을 초기화해주기*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 번들로 값 전달받기
        categoryName = arguments!!.getString("category_name")
        if (arguments!!.getString("sharedLinkURL") != null){
            existSharedLinkURL = true
            sharedLinkURL = arguments!!.getString("sharedLinkURL")
        }

        Log.d("링크 프래그먼트 응답 ", "헬로 onCreate")
    }

    /* 레이아웃 inflate하고 view 객체를 얻는 곳이기 때문에 여기서 view 와 관련된 리소스 초기화*/
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_link_list, container, false)
        setHasOptionsMenu(true)
        addLinkButton = view.findViewById(R.id.bookmark_link_add_btn)
        linkDataListAdapter = BookmarkActivity_link_list_Adapter(view.context, linkDataList_array, this)
        linkDataList_recyclerview = view.findViewById(R.id.bookmark_link_list)
        linkData_list_linearLayoutManager = LinearLayoutManager(view.context)
        linkDialogBuilder = AlertDialog.Builder(view.context)
        linkDialogView = layoutInflater.inflate(R.layout.bookmark_popup_link, null)

        Log.d("링크 프래그먼트 응답 ", "헬로 onCreateView")
        return view
    }

    /* 프래그먼트의 oncreateview 끝나고 액티비티에서의 oncreate 호출되고 난 후에 호출되는 메소드. 액티비티와 프래그먼트가 연결되는 시점.
     * 액티비티와 프래그먼트의 뷰가 모두 생성된 상태로, view를 변경하는 작업이 가능한 단계*/
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // 클릭 이벤트 리스너 붙이기
        setOnClickedListener()
        // 상단바 이름 변경
        var toolbarTitle: TextView = activity!!.findViewById(R.id.toolbar_title)
        toolbarTitle.text = categoryName
        // 링크 리사이클러뷰 변경
        linkDataList_recyclerview!!.adapter = linkDataListAdapter
        linkDataList_recyclerview!!.layoutManager = linkData_list_linearLayoutManager
        linkDataList_recyclerview!!.setHasFixedSize(true)
        getLinkResult = httpConn.get_bookmarkLinkData(categoryName)
        if (getLinkResult != null){
            update_linkDataList(getLinkResult)
        }
        // 다른 앱에서 링크 공유받는 경우
        if (existSharedLinkURL){
            show_AddBookmarkLinkDialog()
        }

        Log.d("링크 프래그먼트 응답 ", "헬로 onActivityCreated")
    }

    /* 프래그먼트가 유저에게 보이도록 해주는 단계. 단계가 매우 빠르게 끝남.
     * 보통 BroadcastReceiver 는 여기서 등록을 하는 것이 좋다.*/
    override fun onStart() {
        super.onStart()

        Log.d("링크 프래그먼트 응답 ", "헬로 onStart")
    }

    /* 프래그먼트가 foreground 에 나올 때 호출되는 메소드. 유저와 상호작용이 가능하게 됨.
     * 화면이 포커스를 가지고 있는 경우에만 사용할 리소스들을 초기화 해준다. onPause 에서 해제해줘야 함.
     * BroadcastReceiver 를 동적으로 생성했거나 SensorManager 로 GPS 사용하는 경우*/
    override fun onResume() {
        super.onResume()
        activity?.invalidateOptionsMenu()

        Log.d("링크 프래그먼트 응답 ", "헬로 onResume")
    }

    /* onPause 에서는 데이터를 저장하거나, 네트워크를 호출하고, 데이터베이스를 실행하면 안된다.
     * 프래그먼트의 부모 액티비티가 아닌, 다른 액티비티가 foreground 로 나오게 되면, onPause 를 호출하고 백스택으로 들어간다.*/
    override fun onPause() {
        super.onPause()

        Log.d("링크 프래그먼트 응답 ", "헬로 onPause")
    }

    /* 다른 액티비티가 화면을 완전히 가리게 되면, onStop()를 호출하게 된다.
     * onStop 에서 프래그먼트는 액티비티의 다른 데이터들과 마찬가지로 유저가 다시 해당 액티비티를 호출하면 다시 복원될 수 있는 상태임.*/
    override fun onStop() {
        super.onStop()

        Log.d("링크 프래그먼트 응답 ", "헬로 onStop")
    }

    /* 프래그먼트와 관련된 view가 제거되는 단계*/
    override fun onDestroyView() {
        super.onDestroyView()

        Log.d("링크 프래그먼트 응답 ", "헬로 onDestroyView")
    }

    /* 프래그먼트 제거하기 직전*/
    override fun onDestroy() {
        super.onDestroy()

        Log.d("링크 프래그먼트 응답 ", "헬로 onDestroy")
    }

    /* 프래그먼트가 액티비티로부터 해제되어질 때 호출됨*/
    override fun onDetach() {
        super.onDetach()

        Log.d("링크 프래그먼트 응답 ", "헬로 onDetach")
    }



    // 생명주기 메소드랑 상관없는 것들 (오버라이드 메소드는 상관있는지 없는지 잘 모름..)
    @SuppressLint("SetTextI18n")
    private fun setOnClickedListener() {
        addLinkButton!!.setOnClickListener{
            Log.d("링크 프래그먼트 응답 ", "링크 추가")
            show_AddBookmarkLinkDialog()

            /*
            // 다이얼로그 두 번 클릭 시 ...you must call removeView()...오류에 대한 해결법
            if (linkDialogView!!.parent != null){
                var dialogParentView = (linkDialogView!!.parent) as ViewGroup
                dialogParentView.removeView(linkDialogView)
            }
            linkDialogBuilder!!.show()
             */
        }
    }

    // menu에 만든 xml파일을 인플레이트 해주는 코드
    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.default_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)

        var searchItem = menu?.findItem(R.id.search)
        var searchView = searchItem?.actionView as SearchView

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            // 검색 입력창에 새로운 텍스트가 들어갈 때 마다 호출
            override fun onQueryTextChange(searchInputString: String?): Boolean {
                return false
            }
            // 검색어를 다 입력하고 서치 버튼을 눌렀을 때
            override fun onQueryTextSubmit(searchInputString: String?): Boolean {
                Toast.makeText(view?.context, searchInputString + "에 대해 검색중", Toast.LENGTH_LONG).show()
                // 여기서 검색 요청하고 데이터 받아오는 걸 처리해야 함.
                return false
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            android.R.id.home -> {
                fragmentManager!!.beginTransaction().remove(this).commit()
                fragmentManager!!.popBackStack()
            }
            R.id.search -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun show_AddBookmarkLinkDialog(){
        var dialog: DialogAddBookMarkLink = DialogAddBookMarkLink()
        var args: Bundle = Bundle()
        args.putString("categoryName", categoryName)
        if (existSharedLinkURL){
            args.putString("sharedLinkURL", sharedLinkURL)
        }
        dialog.arguments = args
        // 다이얼로그프래그먼트 조그만 화면으로
        //dialog.show(activity!!.supportFragmentManager, "addLinkDialog")
        // 다이얼로그 프래그먼트 전체화면으로
        this.fragmentManager!!.beginTransaction()
            .replace(R.id.content, dialog)
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }

    fun update_linkDataList(resultData: String?) {
        if (resultData == "실패"){
            Toast.makeText(this.context, "아직 추가된 링크가 없습니다.", Toast.LENGTH_LONG)
            return
        } else {
            var result_array: JSONArray = JSONArray(resultData)
            var jsonobj_index = result_array.length() - 1
            if (jsonobj_index >= 0) {
                linkDataList_array.removeAll(linkDataList_array)
                for (i in 0..jsonobj_index){
                    var link_title = result_array.getJSONObject(i).getString("link_title")
                    var link_url = result_array.getJSONObject(i).getString("link_url")
                    var link_image_url = result_array.getJSONObject(i).getString("link_image")
                    var link_description = result_array.getJSONObject(i).getString("link_description")
                    var newLinkItem =
                        BookmarkActivity_link(
                            link_url,
                            link_title,
                            link_description
                        )
                    linkDataList_array.add(newLinkItem)
                }
                activity!!.runOnUiThread {
                    linkDataList_recyclerview!!.adapter?.notifyDataSetChanged()
                }
            }
        }
    }


}