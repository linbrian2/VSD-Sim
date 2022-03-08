<template>
  <div>
    <Header
      :title="title"
      :showDate="!isDashboard"
      :clipLeft="!isDashboard"
      :clipRight="isDashboard"
      :actionItems="action_menu_items"
    >
    </Header>
  </div>
</template>

<script>
import AppConstants from '@/utils/constants/app';
import Header from '@/components/common/Header';

export default {
  components: {
    Header
  },

  data: () => ({
    isDashboard: true,
    title: AppConstants.DASHBOARD_APP_TITLE,
    action_menu_items: []
  })
};
</script>
