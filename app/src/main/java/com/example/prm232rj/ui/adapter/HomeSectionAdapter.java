package com.example.prm232rj.ui.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm232rj.data.dto.ComicDtoBanner;
import com.example.prm232rj.data.interfaces.IComicPreview;
import com.example.prm232rj.databinding.ItemComicListHorizontalBinding;
import com.example.prm232rj.databinding.ItemComicsBannerBinding;
import com.example.prm232rj.databinding.ItemSectionHeaderBinding;
import com.example.prm232rj.ui.adapter.section.HomeSectionItem;
import com.example.prm232rj.ui.adapter.section.SectionViewType;
import com.example.prm232rj.ui.screen.Activities.ComicListActivity;

import java.util.List;

public class HomeSectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<HomeSectionItem> sections;

    public HomeSectionAdapter(List<HomeSectionItem> sections) {
        this.sections = sections;
    }

    @Override
    public int getItemViewType(int position) {
        return sections.get(position).viewType;
    }

    @Override
    public int getItemCount() {
        return sections.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == SectionViewType.BANNER) {
            ItemComicsBannerBinding binding = ItemComicsBannerBinding.inflate(inflater, parent, false);
            return new BannerViewHolder(binding);
        } else if (viewType == SectionViewType.SECTION_HEADER) {
            ItemSectionHeaderBinding binding = ItemSectionHeaderBinding.inflate(inflater, parent, false);
            return new SectionHeaderViewHolder(binding);
        } else { // COMIC_LIST
            ItemComicListHorizontalBinding binding = ItemComicListHorizontalBinding.inflate(inflater, parent, false);
            return new ComicListViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        HomeSectionItem item = sections.get(position);
        if (holder instanceof BannerViewHolder) {
            ((BannerViewHolder) holder).bind(item.banners);
        } else if (holder instanceof SectionHeaderViewHolder) {
            ((SectionHeaderViewHolder) holder).bind(item.sectionTitle);
        } else if (holder instanceof ComicListViewHolder) {
            ((ComicListViewHolder) holder).bind(item.comics);
        }

    }

    public void updateBannerSection(List<ComicDtoBanner> banners) {
        for (int i = 0; i < sections.size(); i++) {
            if (sections.get(i).viewType == SectionViewType.BANNER) {
                sections.get(i).banners = banners;
                notifyItemChanged(i);
                return;
            }
        }
    }

    public void updateComicSection(String tag, List<IComicPreview> comics) {
        for (int i = 0; i < sections.size(); i++) {
            if (sections.get(i).viewType == SectionViewType.SECTION_HEADER && tag.equals(sections.get(i).sectionTag)) {
                if (i + 1 < sections.size() && sections.get(i + 1).viewType == SectionViewType.COMIC_LIST) {
                    sections.get(i + 1).comics = comics;
                    notifyItemChanged(i + 1);
                }
                return;
            }
        }
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {
        private final ItemComicsBannerBinding binding;
        private final ComicBannerPagerAdapter adapter = new ComicBannerPagerAdapter();

        public BannerViewHolder(ItemComicsBannerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.bannerViewPager.setAdapter(adapter);
            binding.bannerDots.setViewPager2(binding.bannerViewPager);
        }

        void bind(List<ComicDtoBanner> banners) {
            adapter.setData(banners);
        }
    }

    static class SectionHeaderViewHolder extends RecyclerView.ViewHolder {
        private final ItemSectionHeaderBinding binding;


        public SectionHeaderViewHolder(ItemSectionHeaderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(String titleText) {
            binding.tvSectionTitle.setText(titleText);

            // Tuỳ chỉnh logic theo title (hoặc dùng sectionTag nếu muốn chính xác hơn)
            if (titleText.equals("Hành động")) {
                binding.btnSeeAllHot.setOnClickListener(v -> {
                    Intent intent = new Intent(binding.getRoot().getContext(), ComicListActivity.class);
                    intent.putExtra("TAG_ID", "4"); // Gắn tag phù hợp
                    intent.putExtra("TAG_NAME", "Hành Động");
                    binding.getRoot().getContext().startActivity(intent);
                });
            } else if(titleText.equals("Truyện Hot")){
                // Ẩn hoặc disable nếu không cần xử lý
                binding.btnSeeAllHot.setOnClickListener(v->{
                    Intent intent = new Intent(binding.getRoot().getContext(), ComicListActivity.class);
                    intent.putExtra("TAG_NAME", "Truyện hot");
                    binding.getRoot().getContext().startActivity(intent);
                });

            }else{
                binding.btnSeeAllHot.setOnClickListener(null);
            }
        }
    }

    static class ComicListViewHolder extends RecyclerView.ViewHolder {
        private final ComicPreviewAdapter adapter = new ComicPreviewAdapter(new java.util.ArrayList<>());

        public ComicListViewHolder(ItemComicListHorizontalBinding binding) {
            super(binding.getRoot());
            binding.recyclerView.setLayoutManager(
                    new LinearLayoutManager(binding.getRoot().getContext(),
                            LinearLayoutManager.HORIZONTAL, false));
            binding.recyclerView.setAdapter(adapter);
        }

        void bind(List<IComicPreview> comics) {
            adapter.setData(comics);
        }
    }
}