import { Notification } from '../models/ui_types/notification';

// BE doesn't have a notifications endpoint.
// Return empty array — components should handle empty state.

export const notificationService = {
  getNotifications: async (): Promise<Notification[]> => {
    return [];
  },

  markAsRead: async (_id: string): Promise<void> => {
    // No-op: BE doesn't support this
  },

  markAllAsRead: async (): Promise<void> => {
    // No-op: BE doesn't support this
  },
};
