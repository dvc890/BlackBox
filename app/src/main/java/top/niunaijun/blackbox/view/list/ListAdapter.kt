package top.niunaijun.blackbox.view.list

import android.view.View
import android.view.ViewGroup
import cbfg.rvadapter.RVHolder
import cbfg.rvadapter.RVHolderFactory
import top.niunaijun.blackbox.R
import top.niunaijun.blackbox.bean.InstalledAppBean
import top.niunaijun.blackbox.databinding.ItemPackageBinding

/**
 *
 * @Description: 软件显示界面适配器
 * @Author: wukaicheng
 * @CreateDate: 2021/4/29 21:52
 */
class ListAdapter : RVHolderFactory() {
    override fun createViewHolder(parent: ViewGroup?, viewType: Int, item: Any): RVHolder<out Any> {
        return ListVH(inflate(R.layout.item_package,parent))
    }

    class ListVH(itemView:View) :RVHolder<InstalledAppBean>(itemView) {
        private val binding = ItemPackageBinding.bind(itemView)

        override fun setContent(item: InstalledAppBean, isSelected: Boolean, payload: Any?) {
            binding.icon.setImageDrawable(item.icon)
            binding.name.text = item.name
            binding.packageName.text = item.packageName
            binding.cornerLabel.visibility = if (item.isInstall) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }
}
