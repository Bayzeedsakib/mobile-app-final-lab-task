package com.example.studentcoursemanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CourseAdapter(
    private var courses: MutableList<Course>,
    private val listener: Listener
) : RecyclerView.Adapter<CourseAdapter.CourseViewHolder>() {

    private var fullList: List<Course> = ArrayList(courses)

    interface Listener {
        fun onEdit(course: Course)
        fun onDelete(course: Course)
        fun onOpen(course: Course)
    }

    fun updateList(newList: List<Course>) {
        courses = newList.toMutableList()
        fullList = ArrayList(courses)
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        val q = query.trim().lowercase()
        if (q.isEmpty()) {
            courses = fullList.toMutableList()
        } else {
            courses = fullList.filter {
                it.name.lowercase().contains(q) || it.code.lowercase().contains(q)
            }.toMutableList()
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_course, parent, false)
        return CourseViewHolder(view)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val course = courses[position]
        holder.bind(course)
    }

    override fun getItemCount(): Int = courses.size

    inner class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvCourseName)
        private val tvCode: TextView = itemView.findViewById(R.id.tvCourseCode)
        private val tvInstructor: TextView = itemView.findViewById(R.id.tvInstructor)
        private val tvCredits: TextView = itemView.findViewById(R.id.tvCredits)
        private val btnEdit: ImageButton = itemView.findViewById(R.id.btnEdit)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)

        fun bind(course: Course) {
            tvName.text = course.name
            tvCode.text = course.code
            tvInstructor.text = course.instructor
            tvCredits.text = "${course.credits} cr"

            itemView.setOnClickListener { listener.onOpen(course) }
            btnEdit.setOnClickListener { listener.onEdit(course) }
            btnDelete.setOnClickListener { listener.onDelete(course) }
        }
    }
}

