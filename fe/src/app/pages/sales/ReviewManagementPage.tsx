import { useState, useEffect } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '../../components/ui/card';
import { Badge } from '../../components/ui/badge';
import { Button } from '../../components/ui/button';
import { Input } from '../../components/ui/input';
import { Star, MessageSquare, ShieldAlert, EyeOff, Eye, Loader2, Search, AlertTriangle, ChevronLeft, ChevronRight } from 'lucide-react';
import { useGetAllReviews, useModerateReview, useReplyToReview } from '../../../dataHook/reviewDataHook';
import { ReviewStatus } from '../../../models/ui_types/review';
import { toast } from 'sonner';
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from "../../components/ui/alert-dialog";

export function ReviewManagementPage() {
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [pageInput, setPageInput] = useState('1');
  const [pageSizeInput, setPageSizeInput] = useState('10');
  const [searchTerm, setSearchTerm] = useState('');

  const { data, isLoading } = useGetAllReviews(page, pageSize);
  const reviews = data?.items || [];
  const totalPages = data?.totalPages || 1;
  const totalItems = data?.totalCount || 0;

  // Reset page to 1 when search term changes
  useEffect(() => {
    setPage(1);
  }, [searchTerm]);

  // Sync pageInput with page state
  useEffect(() => {
    setPageInput(String(page));
  }, [page]);

  // Sync pageSizeInput with pageSize state
  useEffect(() => {
    setPageSizeInput(String(pageSize));
  }, [pageSize]);

  // Debounce page size input changes
  useEffect(() => {
    const num = Number(pageSizeInput);
    if (isNaN(num) || num <= 0) return;
    
    const handler = setTimeout(() => {
      if (num !== pageSize) {
        setPageSize(num);
        setPage(1); // Reset to page 1
      }
    }, 500); // 500ms delay
    
    return () => clearTimeout(handler);
  }, [pageSizeInput, pageSize]);

  // Debounce page input changes
  useEffect(() => {
    const num = Number(pageInput);
    if (isNaN(num) || num <= 0 || num > totalPages) return;
    
    const handler = setTimeout(() => {
      if (num !== page) {
        setPage(num);
      }
    }, 500); // 500ms delay
    
    return () => clearTimeout(handler);
  }, [pageInput, page, totalPages]);

  const { mutate: moderateReview } = useModerateReview();
  const { mutate: replyToReview } = useReplyToReview();
  
  const [replyingTo, setReplyingTo] = useState<string | null>(null);
  const [replyText, setReplyText] = useState('');

  // Moderation Confirmation State
  const [confirmingMod, setConfirmingMod] = useState<{ id: string, status: ReviewStatus } | null>(null);
  const [violationReason, setViolationReason] = useState<string>('SPAM');
  const [violationDescription, setViolationDescription] = useState<string>('');

  useEffect(() => {
    setPageInput(String(page));
  }, [page]);

  const handleOpenConfirmMod = (item: { id: string, status: ReviewStatus }) => {
    setViolationReason('SPAM');
    setViolationDescription('');
    setConfirmingMod(item);
  };

  const filteredReviews = reviews.filter(rev => 
    rev.userName.toLowerCase().includes(searchTerm.toLowerCase()) ||
    rev.comment.toLowerCase().includes(searchTerm.toLowerCase()) ||
    (rev.productName || '').toLowerCase().includes(searchTerm.toLowerCase())
  );

  const handleModerate = () => {
    if (!confirmingMod) return;

    if (confirmingMod.status === ReviewStatus.HIDDEN) {
      if (!violationReason) {
        toast.error('Violation reason is required');
        return;
      }
      if (violationReason === 'OTHER' && !violationDescription.trim()) {
        toast.error('Violation description is required when reason is OTHER');
        return;
      }
    }
    
    moderateReview({ 
      id: confirmingMod.id, 
      status: confirmingMod.status,
      reason: confirmingMod.status === ReviewStatus.HIDDEN ? violationReason : undefined,
      description: confirmingMod.status === ReviewStatus.HIDDEN ? violationDescription : undefined
    }, {
      onSuccess: () => {
        toast.success(`Review protocol updated to ${confirmingMod.status}`);
        setConfirmingMod(null);
      },
      onError: (err: any) => {
        toast.error(err.message || 'Failed to update review status');
        setConfirmingMod(null);
      }
    });
  };

  const handleReply = (id: string) => {
    if (!replyText.trim()) return;
    replyToReview({ id, reply: replyText }, {
      onSuccess: () => {
        toast.success('Reply sent');
        setReplyingTo(null);
        setReplyText('');
      },
    });
  };

  return (
    <div className="space-y-6 pb-12">
      <div>
        <h1 className="text-3xl font-bold tracking-tight uppercase">Review Moderation</h1>
        <p className="text-sm text-muted-foreground font-bold uppercase tracking-widest opacity-60">Customer Feedback Protocol</p>
      </div>

      <div className="relative">
        <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
        <Input
          placeholder="Search by user, product, or content..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="pl-10 h-12 rounded-xl border-border bg-card"
        />
      </div>

      {isLoading ? (
        <div className="flex h-64 items-center justify-center">
          <div className="flex flex-col items-center gap-2">
            <Loader2 className="h-8 w-8 animate-spin text-primary" />
            <p className="text-muted-foreground uppercase tracking-widest text-[10px] font-bold">Retrieving Feedback Logs...</p>
          </div>
        </div>
      ) : (
        <div className="space-y-4">
          {filteredReviews.map((review) => (
            <Card key={review.id} className={`border-border shadow-sm rounded-2xl overflow-hidden transition-all ${review.status === ReviewStatus.HIDDEN ? 'opacity-60 bg-muted/20' : 'bg-card'}`}>
              <CardContent className="p-6">
                <div className="flex flex-col gap-6 md:flex-row">
                  <div className="w-full md:w-48 shrink-0 flex flex-col gap-3">
                    <div className="flex items-center gap-2">
                       <div className="h-8 w-8 rounded-full bg-primary flex items-center justify-center text-primary-foreground font-bold text-xs uppercase">
                         {review.userName.charAt(0)}
                       </div>
                       <div>
                         <p className="text-xs font-bold uppercase tracking-tight">{review.userName}</p>
                         <p className="text-[9px] text-muted-foreground font-medium uppercase">{new Date(review.createdAt).toLocaleDateString()}</p>
                       </div>
                    </div>
                    <div className="flex items-center gap-0.5">
                      {[1,2,3,4,5].map(i => (
                        <Star key={i} className={`h-3 w-3 ${i <= review.rating ? 'fill-yellow-400 text-yellow-400' : 'text-muted'}`} />
                      ))}
                    </div>
                    <Badge variant={review.status === ReviewStatus.VISIBLE ? 'success' : 'pending'} className="text-[8px] font-black uppercase tracking-widest w-fit">
                      {review.status}
                    </Badge>
                  </div>

                  <div className="flex-1 space-y-4">
                    <div className="space-y-1">
                       <p className="text-[9px] font-bold uppercase tracking-widest text-muted-foreground">Product Feedback</p>
                       <h4 className="font-bold text-sm uppercase tracking-tight">{review.productName || 'Unknown Product'}</h4>
                    </div>
                    
                    <div className="p-4 rounded-xl bg-muted/30 border border-border/50 italic text-sm text-foreground">
                      "{review.comment}"
                    </div>

                    {/* Multiple Responses */}
                    {review.responses && review.responses.length > 0 ? (
                      <div className="space-y-3">
                        {review.responses.map((resp) => (
                          <div key={resp.id} className="p-4 rounded-xl bg-primary/5 border border-primary/10 space-y-1">
                            <div className="flex items-center justify-between">
                              <div className="flex items-center gap-2">
                                <MessageSquare className="h-3 w-3 text-primary" />
                                <p className="text-[9px] font-bold uppercase tracking-widest text-primary">
                                  {resp.userName || 'Staff Response'}
                                </p>
                              </div>
                              <span className="text-[8px] text-muted-foreground font-medium">
                                {new Date(resp.createdAt).toLocaleDateString()}
                              </span>
                            </div>
                            <p className="text-sm text-foreground font-medium">{resp.content}</p>
                          </div>
                        ))}
                      </div>
                    ) : review.reply ? (
                      /* Legacy single reply fallback */
                      <div className="p-4 rounded-xl bg-primary/5 border border-primary/10 space-y-2">
                        <div className="flex items-center gap-2">
                          <MessageSquare className="h-3 w-3 text-primary" />
                          <p className="text-[9px] font-bold uppercase tracking-widest text-primary">Staff Response</p>
                        </div>
                        <p className="text-sm text-foreground font-medium">{review.reply}</p>
                      </div>
                    ) : null}

                    {replyingTo === review.id ? (
                      <div className="space-y-3 pt-2">
                        <Input 
                          placeholder="Type your protocol response..." 
                          className="h-10 rounded-lg text-xs"
                          value={replyText}
                          onChange={(e) => setReplyText(e.target.value)}
                        />
                        <div className="flex gap-2">
                          <Button size="sm" className="h-8 px-4 rounded-lg text-[9px] font-bold uppercase tracking-widest" onClick={() => handleReply(review.id)}>
                            Send Reply
                          </Button>
                          <Button variant="ghost" size="sm" className="h-8 px-4 rounded-lg text-[9px] font-bold uppercase tracking-widest" onClick={() => setReplyingTo(null)}>
                            Cancel
                          </Button>
                        </div>
                      </div>
                    ) : (
                      <div className="flex gap-2 pt-2">
                        <Button 
                          variant="outline" 
                          size="sm" 
                          className="h-8 px-4 rounded-lg text-[9px] font-bold uppercase tracking-widest border-border"
                          onClick={() => setReplyingTo(review.id)}
                        >
                          <MessageSquare className="h-3.5 w-3.5 mr-2" />
                          {(review.responses && review.responses.length > 0) || review.reply ? 'Reply Again' : 'Reply'}
                        </Button>
                        {review.status === ReviewStatus.VISIBLE && (
                          <Button 
                            variant="ghost" 
                            size="sm" 
                            className="h-8 px-4 rounded-lg text-[9px] font-bold uppercase tracking-widest text-muted-foreground"
                            onClick={() => handleOpenConfirmMod({ id: review.id, status: ReviewStatus.HIDDEN })}
                          >
                            <EyeOff className="h-3.5 w-3.5 mr-2" /> Hide
                          </Button>
                        )}
                        <Button variant="ghost" size="sm" className="h-8 px-4 rounded-lg text-[9px] font-bold uppercase tracking-widest text-destructive hover:bg-destructive/10">
                          <ShieldAlert className="h-3.5 w-3.5 mr-2" />
                          Report
                        </Button>
                      </div>
                    )}
                  </div>
                </div>
              </CardContent>
            </Card>
          ))}

          {/* Premium Pagination Section */}
          <Card className="border-border shadow-sm rounded-2xl overflow-hidden bg-card mt-6">
            <CardContent className="p-0">
              <div className="flex flex-col sm:flex-row items-center justify-between gap-4 p-4 bg-muted/10">
                {/* Left Side: Page Size Selector & Total Records */}
                <div className="flex items-center gap-4">
                  <span className="text-[10px] text-muted-foreground uppercase font-black tracking-widest">
                    Total: {totalItems} reviews
                  </span>
                  
                  <div className="flex items-center gap-2">
                    <span className="text-[10px] text-muted-foreground uppercase font-black tracking-widest">
                      Page Size:
                    </span>
                    <input
                      type="number"
                      value={pageSizeInput}
                      min={1}
                      max={100}
                      onChange={(e) => setPageSizeInput(e.target.value)}
                      className="w-16 h-8 text-xs font-bold text-center border border-border rounded-xl bg-card text-foreground focus:outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary transition-all"
                    />
                  </div>
                </div>

                {/* Right Side: Prev, Quick Jump Input, and Next */}
                <div className="flex items-center gap-3">
                  <Button
                    variant="outline"
                    size="sm"
                    onClick={() => setPage(prev => Math.max(prev - 1, 1))}
                    disabled={page === 1}
                    className="h-8 px-3 rounded-xl border-border bg-card hover:bg-accent text-xs font-bold uppercase tracking-wider gap-1"
                  >
                    <ChevronLeft className="h-3 w-3" />
                    Prev
                  </Button>

                  <div className="flex items-center gap-2">
                    <span className="text-[10px] text-muted-foreground uppercase font-black tracking-widest">
                      Page
                    </span>
                    <input
                      type="number"
                      value={pageInput}
                      min={1}
                      max={totalPages}
                      onChange={(e) => setPageInput(e.target.value)}
                      onKeyDown={(e) => {
                        if (e.key === 'Enter') {
                          const val = Math.max(1, Math.min(totalPages, Number(pageInput)));
                          setPage(val);
                          setPageInput(String(val));
                        }
                      }}
                      onBlur={() => {
                        const val = Math.max(1, Math.min(totalPages, Number(pageInput)));
                        setPage(val);
                        setPageInput(String(val));
                      }}
                      className="w-12 h-8 text-xs font-bold text-center border border-border rounded-xl bg-card text-foreground focus:outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary transition-all"
                    />
                    <span className="text-[10px] text-muted-foreground uppercase font-black tracking-widest">
                      of {totalPages}
                    </span>
                  </div>

                  <Button
                    variant="outline"
                    size="sm"
                    onClick={() => setPage(prev => Math.min(prev + 1, totalPages))}
                    disabled={page >= totalPages}
                    className="h-8 px-3 rounded-xl border-border bg-card hover:bg-accent text-xs font-bold uppercase tracking-wider gap-1"
                  >
                    Next
                    <ChevronRight className="h-3 w-3" />
                  </Button>
                </div>
              </div>
            </CardContent>
          </Card>
        </div>
      )}

      {/* Moderation Confirmation Dialog */}
      <AlertDialog open={!!confirmingMod} onOpenChange={(open) => !open && setConfirmingMod(null)}>
        <AlertDialogContent className="rounded-2xl border-border bg-card shadow-2xl">
          <AlertDialogHeader>
            <div className="flex items-center gap-3 mb-2">
               <div className="p-2 rounded-xl bg-primary/10 text-primary">
                 <AlertTriangle className="h-5 w-5" />
               </div>
               <AlertDialogTitle className="text-sm font-bold uppercase tracking-tight">
                 Hide Feedback?
               </AlertDialogTitle>
            </div>
            <AlertDialogDescription className="text-xs font-medium text-muted-foreground leading-relaxed italic">
              Are you sure you want to hide this review from the public protocol? It will no longer be visible on the product detail page.
            </AlertDialogDescription>
          </AlertDialogHeader>

          <div className="space-y-4 my-4">
            <div className="space-y-1.5">
              <label className="text-[10px] font-bold uppercase tracking-widest text-muted-foreground">Violation Reason</label>
              <select
                value={violationReason}
                onChange={(e) => setViolationReason(e.target.value)}
                className="w-full h-10 px-3 rounded-xl border border-border bg-card text-xs font-medium focus:outline-none focus:ring-2 focus:ring-primary/20"
              >
                <option value="SPAM">SPAM (Advertisement, fake feedback)</option>
                <option value="OFFENSIVE">OFFENSIVE (Abusive, inappropriate language)</option>
                <option value="OTHER">OTHER (Custom reason)</option>
              </select>
            </div>
            <div className="space-y-1.5">
              <label className="text-[10px] font-bold uppercase tracking-widest text-muted-foreground">
                Violation Description {violationReason === 'OTHER' && <span className="text-destructive">*</span>}
              </label>
              <textarea
                placeholder={violationReason === 'OTHER' ? "Describe the specific reason for hiding this feedback..." : "Optional details..."}
                value={violationDescription}
                onChange={(e) => setViolationDescription(e.target.value)}
                disabled={violationReason !== 'OTHER'}
                className={`w-full min-h-[80px] p-3 rounded-xl border border-border bg-card text-xs font-medium focus:outline-none focus:ring-2 focus:ring-primary/20 ${violationReason !== 'OTHER' ? 'opacity-50 cursor-not-allowed bg-muted/30' : ''}`}
              />
            </div>
          </div>
          <AlertDialogFooter className="mt-6">
            <AlertDialogCancel className="h-10 rounded-xl text-[10px] font-bold uppercase tracking-widest border-border">Cancel</AlertDialogCancel>
            <AlertDialogAction 
              onClick={handleModerate}
              className="h-10 rounded-xl text-[10px] font-bold uppercase tracking-widest text-white bg-primary hover:bg-primary/90"
            >
              Hide Review
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  );
}
