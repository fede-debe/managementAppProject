package com.example.managementappproject.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.managementappproject.R
import com.example.managementappproject.activities.TaskListActivity
import com.example.managementappproject.models.Task
import kotlinx.android.synthetic.main.item_task.view.*

/** Now that we set the RecyclerView we can also create an adapter for it */
open class TaskListItemsAdapter(private val context: Context, private var list: ArrayList<Task>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        // we want to inflate our View from here
        val view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false)
        /* we need to set LinearLayout as the LayoutParameters, we can define the parameters of the linearLayout we want to use
           the ViewHolder need to be 0.7 times width of the parent(of the screen available) and make and Int out of it(LayoutParams
           required  an Int. We set the Height as WRAP_CONTENT ( as much is required) */
        val layoutParams = LinearLayout.LayoutParams(
                (parent.width * 0.7).toInt(), LinearLayout.LayoutParams.WRAP_CONTENT
        )
        // now we can set others attributes for out layout - layoutParams is a var that we can use to add as much as params we need (15 to left, 40 to right)
        layoutParams.setMargins((15.toDP().toPx()), 0, (40.toDP()).toPx(), 0)
        // we set these params for our view
        view.layoutParams = layoutParams
        // now we can return MyViewHolder and pass to it the view that we prepared
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //we get the position and create our model. whatever list we get at any position given, we will get the model from it(Task)
        val model = list[position]
        if (holder is MyViewHolder) {
            // if we don't have an entry in our list we want to display only tv_add_task_list
            if (position == list.size - 1) {
                holder.itemView.tv_add_task_list.visibility = View.VISIBLE
                holder.itemView.ll_task_item.visibility = View.GONE
            } else {
                // if we have an entry in our list we want to display only ll_task_item
                holder.itemView.tv_add_task_list.visibility = View.GONE
                holder.itemView.ll_task_item.visibility = View.VISIBLE
            }

            // we want to set a text for the taskList title, we get it from the title of the model that is passed
            holder.itemView.tv_task_list_title.text = model.title
            // we need to set also a OnClickListener for it(not the title, for the actual list)
            holder.itemView.tv_add_task_list.setOnClickListener {
                holder.itemView.tv_add_task_list.visibility = View.GONE
                holder.itemView.cv_add_task_list_name.visibility = View.VISIBLE // when the user click on this btn we want to show this View instead of the btn
            }

            // depending of which btn we press, we'll have different reactions displayed in the UI.
            holder.itemView.ib_close_list_name.setOnClickListener {
                holder.itemView.tv_add_task_list.visibility = View.VISIBLE
                holder.itemView.cv_add_task_list_name.visibility = View.GONE
            }

            holder.itemView.ib_done_list_name.setOnClickListener {
                // create entry in DB and display the taskList
                val listName = holder.itemView.et_task_list_name.text.toString() // we get the text that is entered to this view when we press the btn
                // we need to check if the listName exist to check some conditions
                if (listName.isNotEmpty()){
                    if (context is TaskListActivity){ // if this is the case
                        context.createTaskList(listName) // context from the taskListActivity and we pass the listName to it and use this method
                    }
                  }else{ // if it is empty we show a Toast.message to the user if he didn't enter a title
                      Toast.makeText(context, "Please Enter List Name.", Toast.LENGTH_SHORT).show()
                }
            }
            // editing and deleting lists
            holder.itemView.ib_edit_list_name.setOnClickListener {
                holder.itemView.et_edit_task_list_name.setText(model.title) // we set the text with the title we get from the model
                holder.itemView.ll_title_view.visibility = View.GONE
                holder.itemView.cv_edit_task_list_name.visibility = View.VISIBLE
            }

            holder.itemView.ib_close_editable_view.setOnClickListener {
                holder.itemView.ll_title_view.visibility = View.VISIBLE
                holder.itemView.cv_edit_task_list_name.visibility = View.GONE
            }

            // we pass a functionality to make something happen
            holder.itemView.ib_done_edit_list_name.setOnClickListener{
                // we need the listName and we get it from this itemView
                val listName = holder.itemView.et_edit_task_list_name.text.toString()
                // conditions for the case
                if (listName.isNotEmpty()){
                    if (context is TaskListActivity){
                        context.updateTaskList(position, listName, model) // if the title is not empty we update the taskList
                    } //(position from onBindViewHolder and model based on the position, listName from editText we used here).
                }else{
                    Toast.makeText(context, "Please Enter a List Name.", Toast.LENGTH_SHORT).show() // otherwise
                }
            }
            // implementing the code to delete a taskList
            holder.itemView.ib_delete_list.setOnClickListener{
                alertDialogForDeleteList(position, model.title) // inside the method we make the actual deleting
            }
            // implementing the tv_add_card functionality to show the cv_add_card View and add an element to the list
            holder.itemView.tv_add_card.setOnClickListener{
                holder.itemView.tv_add_card.visibility = View.GONE
                holder.itemView.cv_add_card.visibility = View.VISIBLE
            }
            // implement the functionality to cancel the process of adding a new element
            holder.itemView.ib_close_card_name.setOnClickListener{
                holder.itemView.tv_add_card.visibility = View.VISIBLE
                holder.itemView.cv_add_card.visibility = View.GONE
            }
            // implement the functionality where the actual creation of the card happens
            holder.itemView.ib_done_card_name.setOnClickListener{
                val cardName = holder.itemView.et_card_name.text.toString()
                if (cardName.isNotEmpty()){
                    if (context is TaskListActivity){
                        context.addCardToTaskList(position, cardName)// position from onBindViewHolder, cardName from the TextView et_card_name
                    }
                } else {
                    Toast.makeText(context, "Please Enter a Card Name", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun alertDialogForDeleteList(position: Int, title: String){
        val builder = AlertDialog.Builder(context)
        // set title for alert dialog
        builder.setTitle("Alert")
        // set message for alert dialog
        builder.setMessage("Are you sure you want to delete $title ?")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        // performing positive icon
        builder.setPositiveButton("Yes") { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed

            if (context is TaskListActivity){
                context.deleteTaskList(position)
            }
        }
        // performing negative action
        builder.setNegativeButton("No") { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false) // Will not allow user to cancel after clicking on remaining screen area.
        alertDialog.show() // show the dialog to UI
    }

    /** we don't want the recyclerView to get the 100% of the layout. we need a method to calculate the width in
        density pixel to occupy the 70% of the screen with the recyclerView. This method allow us to get the density of
        the screen and convert it to an Int value to see how big is the density to adjust the width of the View*/
    private fun Int.toDP(): Int = (this / Resources.getSystem().displayMetrics.density).toInt()

    /**  method to get the pixel from the density pixel, the opposite way to the 1st method */
    private fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

    /** This ViewHolder describes an ItemView and the metadata about its place within the RecyclerView */
    class MyViewHolder(view: View): RecyclerView.ViewHolder(view)
}