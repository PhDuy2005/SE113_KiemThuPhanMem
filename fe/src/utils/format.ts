export const formatCurrency = (amount: number): string => {
  if (amount === undefined || amount === null || isNaN(amount)) return '$0.00';
  const parts = Number(amount).toFixed(2).split('.');
  parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ' ');
  return `$${parts.join('.')}`;
};

export const formatDate = (dateString: string): string => {
  return new Date(dateString).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  });
};

export const formatImageUrl = (imageUrl?: string | null): string => {
  if (!imageUrl) {
    return '';
  }
  if (imageUrl.startsWith('http://') || imageUrl.startsWith('https://')) {
    return imageUrl;
  }
  const base = (import.meta as any).env.VITE_API_BASE_URL || 'http://localhost:8080';
  try {
    return new URL(imageUrl, base).toString();
  } catch (e) {
    return imageUrl;
  }
};

